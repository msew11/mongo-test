package org.matrix.game.dao

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.WriteConcern
import com.mongodb.client.MongoClients
import jakarta.persistence.Entity
import org.hibernate.cfg.Configuration
import org.matrix.game.entity.IDocument
import org.reflections.Reflections
import org.springframework.data.convert.TypeInformationMapper
import org.springframework.data.mapping.Alias
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.util.ClassTypeInformation
import org.springframework.data.util.TypeInformation
import java.lang.reflect.Modifier
import java.util.concurrent.TimeUnit

/**
 * [java sync 版本兼容性](https://www.mongodb.com/zh-cn/docs/drivers/java/sync/current/compatibility/)
 */
class DaoFactory {

    companion object {

        @JvmStatic
        fun createMongoDao(): MongoDbDao {
            val db_username = "admin"
            val db_password = "123456"
            val hostname = "localhost"
            val port = 27017
            val auth_db = "admin"
            val setting = MongoClientSettings.builder()
                .writeConcern(WriteConcern.W1)
                .applyConnectionString(ConnectionString("mongodb://${hostname}:${port}"))
                //.applyConnectionString(ConnectionString("mongodb://${db_username}:${db_password}@${hostname}:${port}/${auth_db}?connectTimeoutMS=2000"))
                .credential(MongoCredential.createCredential(db_username, auth_db, db_password.toCharArray()))
                .applyToConnectionPoolSettings { builder ->
                    builder
                        .minSize(1)
                        .maxSize(100)
                        .maxWaitTime(120, TimeUnit.SECONDS)
                        .maxConnectionLifeTime(0, TimeUnit.SECONDS)
                        .build()
                }
                .build()

            val client = MongoClients.create(setting)
            val mongoDbFactory = SimpleMongoClientDatabaseFactory(client, "mydb")
            val converter = MappingMongoConverter(DefaultDbRefResolver(mongoDbFactory), MongoMappingContext())
            // 查找会根据这里设置的_class去读把
            converter.typeMapper = DefaultMongoTypeMapper("_class", listOf(CustomTypeInformationMapper()))
            val mongoDbDao = MongoDbDao(client, MongoTemplate(mongoDbFactory, converter))

            check(mongoDbDao, converter)

            return mongoDbDao
        }

        @JvmStatic
        fun check(dao: MongoDbDao, converter: MappingMongoConverter) {
            CustomTypeInformationMapper.packPath.forEach { path ->
                val classes = Reflections(path).getSubTypesOf(IDocument::class.java)
                    .filter { !Modifier.isAbstract(it.modifiers) }
                    .filter { !Modifier.isInterface(it.modifiers) }
                classes.forEach { clazz ->
                    val instance = clazz.getDeclaredConstructor().newInstance()
                    val document = dao.convert(instance)
                    // println("document: $document")
                    val entity = converter.read(clazz, document)
                    // println("entity: $entity")
                }
            }
        }

        fun createHibernateDao(): HibernateDao {
            val host = "localhost:3306"
            val dbName = "game_matrix"
            val username = "root"
            val password = "123456"


            val url = "jdbc:mysql://${host}/${dbName}?createDatabaseIfNotExist=true"
            val hibernateCfg = Configuration().configure("hibernate.cfg.xml")
            hibernateCfg.setProperty("hibernate.connection.url", url)
            hibernateCfg.setProperty("hibernate.connection.username", username)
            hibernateCfg.setProperty("hibernate.connection.password", password)

            val scanClass = Reflections("org.matrix.game")
                .getTypesAnnotatedWith(Entity::class.java)
            scanClass.forEach {
                hibernateCfg.addAnnotatedClass(it)
            }

            return HibernateDao(hibernateCfg.buildSessionFactory())
        }
    }

//    fun insert(cli: MongoClient) {
//        val mydb = cli.getDatabase("mydb")
//
//        val players = mydb.getCollection("players")
//        val result = players.insertOne(
//            Document()
//                .append("_id", ObjectId())
//                .append("name", "张三")
//        )
//
//        println("insert success: ${result.insertedId}")
//    }

}

class CustomTypeInformationMapper : TypeInformationMapper {

    companion object {
        val documentClassMap: HashMap<String, TypeInformation<*>> = hashMapOf()
        val packPath = arrayOf(
            "org.matrix.game.entity",
        )

        init {
            packPath.forEach { path ->
                val classes = Reflections(path).getSubTypesOf(IDocument::class.java)
                    .filter { !Modifier.isAbstract(it.modifiers) }
                    .filter { !Modifier.isInterface(it.modifiers) }
                classes.forEach { clazz ->
                    documentClassMap[clazz.name] = ClassTypeInformation.from(clazz)
                }
            }

//            println("documentClassMap: $documentClassMap")
        }
    }

    override fun resolveTypeFrom(alias: Alias): TypeInformation<*>? {
        val stringAlias = alias.mapTyped(String::class.java) ?: return null
        documentClassMap[stringAlias]?.apply {
            return this
        }
        throw RuntimeException("TypeInformationMapper没有找到类型: $stringAlias")
    }

    override fun createAliasFor(type: TypeInformation<*>): Alias {
        return Alias.of(type.type.name)
    }

}
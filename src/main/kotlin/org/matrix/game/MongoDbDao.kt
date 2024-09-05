package org.matrix.game

import org.bson.Document
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.UpdateDefinition

class MongoDbDao(
    val operations: MongoOperations
) {
    fun convert(entity: Any): Document {
        val document = Document()
        operations.converter.write(entity, document)
        return document
    }

    fun <T> findById(clazz: Class<T>, id: Any): T? {
        return operations.findById(id, clazz)
    }

    fun insert(entity: Any) {
        operations.insert(entity, getCollectionName(entity.javaClass))
    }

    fun save(entity: Any) {
        operations.save(entity, getCollectionName(entity.javaClass))
    }

    fun update(query: Query, update: UpdateDefinition, clazz: Class<out RootDocument>) {
        operations.updateFirst(query, update, clazz)
    }

    /**
     * collectionName相当于表名，取类的首字母小写作为表名
     */
    fun getCollectionName(clazz: Class<*>): String {
        return getCollectionName(this, clazz)
    }

    companion object {
        fun getCollectionName(dao: MongoDbDao, clazz: Class<*>): String {
            val className = clazz.simpleName
            return className.first().lowercaseChar() + className.substring(1)
        }
    }
}


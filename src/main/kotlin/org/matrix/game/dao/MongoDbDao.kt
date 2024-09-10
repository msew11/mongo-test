package org.matrix.game.dao

import com.mongodb.client.MongoClient
import org.bson.Document
import org.matrix.game.entity.RootDocument
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.BulkOperations.BulkMode
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.UpdateDefinition

class MongoDbDao(
    val client: MongoClient,
    val operations: MongoTemplate
) {
    fun convert(entity: Any): Document {
        val document = Document()
        operations.converter.write(entity, document)
        return document
    }

    fun <T> findById(clazz: Class<T>, id: Any, name: String): T? {
        return operations.findById(id, clazz, name)
    }

    fun insert(entity: Any, name: String) {
        operations.insert(entity, name)
    }

    fun bulkInsert(entity: Any, name: String) {
        operations.bulkOps(BulkMode.ORDERED, name).insert(entity)
    }

    fun insertMany(entityList: List<Any>, name: String) {
        operations.insert(entityList, name)
    }

    fun save(entity: Any) {
        operations.save(entity, getCollectionName(entity.javaClass))
    }

    fun update(query: Query, update: UpdateDefinition, clazz: Class<out RootDocument>) {
        operations.updateFirst(query, update, clazz)
    }

    fun execWithTransaction(name: String, operate: (BulkOperations) -> Unit) {
        findWithTransaction(name) { operate(it) }
    }

    fun <R> findWithTransaction(name: String, query: (BulkOperations) -> R): R {
        val bulk = operations.withSession(client.startSession()).bulkOps(BulkMode.ORDERED, name)
        try {

            val result = query(bulk)

            bulk.execute()

            return result
        } catch (e: Exception) {
            throw e
        } finally {
        }
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


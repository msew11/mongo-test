package org.matrix.game.dao

import java.io.Serializable

interface CommonDao {

    fun save(entity: IEntity)

    fun <T : IEntity> findById(clazz: Class<T>, id: Serializable): T?

    fun update(entity: IEntity)

    fun saveOrUpdate(entity: IEntity)

    fun delete(entity: IEntity)

//    fun <R> findWithTransaction(query: (Session) -> R): R

//    fun execWithTransaction(operate: (Session) -> Unit)

    fun close()
}

/**
 * 标记持久化实体
 *
 */
interface IEntity : Serializable {
    fun primaryKey(): Serializable
}
package org.matrix.game.dao

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction

class HibernateDao(val sessionFactory: SessionFactory) {

    fun save(entity: Any) {
        execWithTransaction { it.save(entity) }
    }

    fun execWithTransaction(operate: (Session) -> Unit) {
        findWithTransaction { operate(it) }
    }

    fun <R> findWithTransaction(query: (Session) -> R): R {
        val session = sessionFactory.openSession()
        var transaction: Transaction? = null
        try {
            transaction = session.beginTransaction()
            transaction.timeout = 300

            val result = query(session)

            transaction.commit()

            return result
        } catch (e: Exception) {
            transaction?.rollback()
            throw e
        } finally {
            session.close()
        }
    }

    fun close() {
        sessionFactory.close()
    }
}
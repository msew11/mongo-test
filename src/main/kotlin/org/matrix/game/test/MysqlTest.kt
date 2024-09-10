package org.matrix.game.test

import org.matrix.game.dao.DaoFactory
import org.matrix.game.dao.HibernateDao
import org.matrix.game.entity.PlayerEntity
import java.util.*


fun testMysql() {
    val dao = DaoFactory.createHibernateDao()

    testBatchInsert(dao)

    dao.close()
}

fun testBatchInsert(dao: HibernateDao) {
    val pl = LinkedList<PlayerEntity>()
    for (id in 1..10000L) {
        pl.add(
            PlayerEntity().apply {
                this.id = id
                this.name = "player-$id"
                test = "testMsg"
            }
        )
    }

    val start = System.currentTimeMillis()
    println("start: $start")

    dao.execWithTransaction {session ->
        pl.forEach { session.save(it) }
    }

    val end = System.currentTimeMillis()
    println("start: $end")
    println("${end - start} ms")

}
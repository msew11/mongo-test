package org.matrix.game.test

import org.matrix.game.dao.DaoFactory
import org.matrix.game.dao.MongoDbDao
import org.matrix.game.entity.Player
import org.matrix.game.entity.Role
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.where
import java.util.*


fun testMongo() {

    val dao = DaoFactory.createMongoDao()

    testBatchInsert(dao, "player2")
    //testUpdate(dao)
    // testSave(dao, "李四")
    //    testFindById(dao)
}

fun testUpdate(dao: MongoDbDao) {
    val query = Query.query(where(Player::id).isEqualTo(10001))
    val update = Update.update("role", Role(worldId = 1L, nickname = "王五"))
    dao.update(query, update, Player::class.java)
}

fun testFindById(dao: MongoDbDao, name: String) {
    val rs = dao.findById(Player::class.java, 10001L, name)
    println(rs)
}

fun testBatchFindById(dao: MongoDbDao, name: String) {
    val start = System.currentTimeMillis()
    println("start: $start")

    for (id in 1..10000L) {
        val rs = dao.findById(Player::class.java, id, name)
        if (rs == null) {
            throw RuntimeException("未找到玩家")
        }
    }

    val end = System.currentTimeMillis()
    println("end: $end")
    println("${end - start} ms")
}

fun testInsert(dao: MongoDbDao, id: Long, name: String) {
    val player = Player(
        id = id,
        openId = "$id",
        Role(
            worldId = 1L,
            nickname = "player-$id"
        )
    )

    dao.insert(player, name)
}

fun testBatchInsert(dao: MongoDbDao, name: String) {
    val pl = LinkedList<Player>()
    for (id in 1..10000L) {
        pl.add(
            Player(
                id = id,
                openId = "$id",
                Role(
                    worldId = 1L,
                    nickname = "player-$id"
                )
            )
        )
    }

    val start = System.currentTimeMillis()
    println("start: $start")

    dao.execWithTransaction(name) { buik ->


        pl.forEachIndexed { i, it ->
            buik.insert(it)
            if (i == 5000) {
                throw RuntimeException("我异常了")
            }
        }

    }

    val end = System.currentTimeMillis()
    println("start: $end")
    println("${end - start} ms")

}

fun testInsertMany(dao: MongoDbDao) {
    val pl = LinkedList<Player>()
    for (id in 1..10000L) {
        pl.add(
            Player(
                id = id,
                openId = "$id",
                Role(
                    worldId = 1L,
                    nickname = "player-$id"
                )
            )
        )
    }

    val start = System.currentTimeMillis()
    println("start: $start")

    dao.insertMany(pl, "player2")
    val end = System.currentTimeMillis()
    println("start: $end")
    println("${end - start} ms")

}

fun testSave(dao: MongoDbDao, nickname: String) {

    val player = Player(
        id = 10001L,
        openId = "10001",
        Role(
            worldId = 1L,
            nickname = nickname
        )
    )

    dao.save(player)
}
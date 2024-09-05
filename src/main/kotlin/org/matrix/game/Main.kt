package org.matrix.game

import org.matrix.game.entity.Player
import org.matrix.game.entity.Role
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.where

fun main() {
    val dao = MongoTest.createDao()

    //testInsert(dao)
    testUpdate(dao)
    // testSave(dao, "李四")
//    testFindById(dao)
}

fun testUpdate(dao: MongoDbDao) {
    val query = Query.query(where(Player::id).isEqualTo(10001))
    val update = Update.update("role", Role(worldId = 1L, nickname = "王五"))
    dao.update(query, update, Player::class.java)
}

fun testFindById(dao: MongoDbDao) {
    val rs = dao.findById(Player::class.java, 10001L)
    println(rs)
}

fun testInsert(dao: MongoDbDao) {
    val player = Player(
        id = 10001L,
        openId = "10001",
        Role(
            worldId = 1L,
            nickname = "张三"
        )
    )

    dao.insert(player)
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
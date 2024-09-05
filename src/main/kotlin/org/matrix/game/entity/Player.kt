package org.matrix.game.entity

import org.matrix.game.RootDocument
import org.matrix.game.SubDocument
import org.springframework.data.annotation.Id

/**
 * `@Id`标注的字段在db中为`_id`主键字段
 */
class Player(
    @Id
    val id: Long = 0,
    val openId: String = "",
    val role: Role = Role(),
    /** 道具 */
    val items: HashMap<Int, Item> = hashMapOf(),
) : RootDocument {

}

data class Role(
    var worldId: Long = 0,
    var nickname: String = ""
) : SubDocument {

}

data class Item(
    val itemId: Int = 0,
    var amount: Long = 0
) : SubDocument
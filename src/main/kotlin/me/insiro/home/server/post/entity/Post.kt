package me.insiro.home.server.post.entity

import kotlinx.serialization.Serializable
import me.insiro.home.server.application.domain.BaseEntityVO
import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Posts : IntIdTable() {
    val title = varchar("title", 100)
    val status = enumeration<Status>("status")
    val authorId = reference("authorId", Users.id)
    val category = reference("category", Categories.id)
}

data class Post(
    var title: String,
    var status: Status,
    var authorId: User.Id,
    var category: Category.Id,
    val comments:List<Comment>  = listOf(),
    override val id: Id? = null,
) : BaseEntityVO() {
    @JvmInline
    @Serializable
    value class Id(override val value: Long) : BaseEntityVO.Id {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}

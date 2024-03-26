package me.insiro.home.server.post.entity

import kotlinx.serialization.Serializable
import me.insiro.home.server.application.domain.IBaseEntityID
import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.application.domain.TitledTable
import me.insiro.home.server.application.domain.TitledVO
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

object Posts : TitledTable() {
    val status = enumeration<Status>("status")
    val authorId = reference("authorId", Users.id)
    val categoryId = reference("category", Categories.id).nullable()
}

sealed interface Post : TitledVO {
    var status: Status
    override val id:Id?

    @JvmInline
    @Serializable
    value class Id(override val value: Long) : IBaseEntityID {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }


    data class Raw(
        override var title: String,
        override var status: Status,
        var authorId: User.Id,
        var categoryId: Category.Id?,
        override val id: Id? = null,
        override val createdAt: LocalDateTime? = null,
    ) : Post

    data class Joined(
        override var title: String,
        override var status: Status,
        var author: AuthorInfo,
        var category: Category? = null,
        override val id: Id? = null,
        override val createdAt: LocalDateTime? = null,
    ) : Post {
        data class AuthorInfo(val id: User.Id, val name: String)
    }

}


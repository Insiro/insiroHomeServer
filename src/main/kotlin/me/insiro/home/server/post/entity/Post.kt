package me.insiro.home.server.post.entity

import me.insiro.home.server.application.domain.entity.*
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object Posts : UUIDBaseTable(), ITitledTable, TableCreatedAt {
    override val title: Column<String> = varchar("title", 100)
    val status = enumeration<Status>("status")
    val authorId = reference("authorId", Users.id)
    val categoryId = reference("category", Categories.id).nullable()
    override val createdAt = datetime("createdAt").clientDefault { LocalDateTime.now() }
}

sealed interface Post : UUIDEntityVO, TitledVO, ICreatedAt {
    var status: Status
    override val id: Id?

    @JvmInline
    value class Id(override val value: UUID) : IEntityVO.Id<UUID> {
        constructor(entityID: EntityID<UUID>) : this(entityID.value)
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


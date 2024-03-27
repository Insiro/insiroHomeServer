package me.insiro.home.server.post.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.insiro.home.server.application.domain.entity.LongBaseTable
import me.insiro.home.server.application.domain.entity.LongEntityVO
import me.insiro.home.server.application.domain.entity.LongID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.json.json
import java.time.LocalDateTime


object Comments : LongBaseTable() {
    val content = varchar("content", 300)
    val postId = reference("postId", Posts.id)
    val parentId = long("parentId").nullable()
    val authorInfo = json<CommentUserInfo>("author_json", Json { prettyPrint = true })
}

data class Comment(
    var content: String,
    var postId: Post.Id,
    var parentId: Id?,
    var author: CommentUserInfo,
    override val id: Id? = null,
    override val createdAt: LocalDateTime? = null,
) : LongEntityVO {
    @JvmInline
    @Serializable
    value class Id(override val value: Long) : LongID {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}
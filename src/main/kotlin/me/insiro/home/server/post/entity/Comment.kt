package me.insiro.home.server.post.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.insiro.home.server.application.domain.BaseEntityVO
import me.insiro.home.server.application.domain.BaseIDTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.json.json



object Comments : BaseIDTable() {
    val content = varchar("content", 300)
    val postId = reference("postId", Posts.id)
    val parentId = reference("parentId", Comments.id).nullable()
    val authorInfo = json<CommentUserInfo>("author_json", Json { prettyPrint = true })
}

data class Comment(
    var content: String,
    var postId: Post.Id,
    var parentId: Id?,
    var author: CommentUserInfo,
    override val id: Id? = null,
) : BaseEntityVO() {
    @JvmInline
    @Serializable
    value class Id(override val value: Long) : BaseEntityVO.Id {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}
package me.insiro.home.server.post.repository

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.application.domain.OffsetLimit
import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.Comments
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.user.entity.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CommentRepository : AbsRepository<Long, Comments, Comment, Comment.Id> {
    override val table = Comments

    override fun relationObjectMapping(it: ResultRow): Comment {
        return Comment(
            it[Comments.content],
            Post.Id(it[Comments.postId].value),
            it[Comments.parentId]?.value?.let { id -> Comment.Id(id) },
            it[Comments.authorInfo],
            Comment.Id(it[Comments.id].value)

        )
    }

    override fun update(vo: Comment): Comment = transaction {
        assert(vo.id != null)
        Comments.update {
            Users.id eq vo.id!!.value
            it[content] = vo.content
        }
        vo
    }


    override fun new(vo: Comment): Comment = transaction {
        val id = Comments.insertAndGetId {
            it[content] = vo.content
            it[postId] = vo.postId.value
            it[createdAt] = LocalDateTime.now()
            it[parentId] = vo.parentId?.value
            it[authorInfo] = vo.author
        }
        vo.copy(id = Comment.Id(id.value))
    }

    fun find(postId: Post.Id? = null, offsetLimit: OffsetLimit? = null): List<Comment> = transaction {
        val query = Comments.selectAll()
        postId?.let { query.adjustWhere { Comments.id eq postId.value } }
        offsetLimit?.let { query.limit(offsetLimit.limit, offsetLimit.offset) }
        query.map { relationObjectMapping(it) }
    }

    fun delete(postId: Post.Id): Int = transaction {
        Comments.deleteWhere { Comments.postId eq postId.value }
    }
}
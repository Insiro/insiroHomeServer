package me.insiro.home.server.post.repository

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.post.entity.Categories
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.post.entity.Posts
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class PostRepository : AbsRepository<UUID, Posts, Post.Raw, Post.Id> {
    override val table = Posts


    override fun relationObjectMapping(it: ResultRow): Post.Raw {
        return Post.Raw(
            it[Posts.title],
            it[Posts.status],
            User.Id(it[Posts.authorId].value),
            it[Posts.categoryId]?.let { Category.Id(it) },
            Post.Id(it[Posts.id].value),
            it[Posts.createdAt]
        )
    }

    fun joiningQuery(offsetLimit: OffsetLimit? = null, joinCategory: Boolean = false): Query {
        var selectColumns = Posts.columns + Users.name
        val query = Posts.join(Users, JoinType.LEFT, onColumn = Posts.authorId, otherColumn = Users.id)
            .apply {
                if (!joinCategory) return@apply
                this.join(Categories, JoinType.LEFT, onColumn = Posts.categoryId, otherColumn = Categories.id)
                selectColumns = selectColumns + Categories.name
            }
            .select(selectColumns)
        offsetLimit?.apply { query.limit(this.limit, this.offset) }
        return query
    }

    fun findByIdJoining(id: Post.Id): Post.Joined? = transaction {
        joiningQuery(null).where { Posts.id eq id.value }.map { joinedRelationObjectMapping(it) }.firstOrNull()
    }

    fun joinedRelationObjectMapping(it: ResultRow): Post.Joined {
        return Post.Joined(
            it[Posts.title],
            it[Posts.status],
            Post.Joined.AuthorInfo(User.Id(it[Posts.authorId].value), it[Users.name]),
            it[Posts.categoryId]?.let { id -> Category(it[Categories.name], id = Category.Id(id)) },
            Post.Id(it[Posts.id].value),
            it[Posts.createdAt]
        )
    }

    override fun new(vo: Post.Raw): Post.Raw = transaction {
        val created = LocalDateTime.now()

        val id = Posts.insertAndGetId {
            it[authorId] = vo.authorId.value
            it[categoryId] = vo.categoryId?.value
            it[status] = vo.status
            it[createdAt] = created
            it[title] = vo.title
        }
        vo.copy(id = Post.Id(id), createdAt = created)
    }

    fun new(vo: Post.Raw, id: Post.Id): Post.Raw? = transaction {
        if (Posts.select(Posts.id).where { Posts.id eq id.value }.count() != 0L)
            return@transaction null

        val created = LocalDateTime.now()
        Posts.insert {
            it[authorId] = vo.authorId.value
            it[categoryId] = vo.categoryId?.value
            it[status] = vo.status
            it[createdAt] = created
            it[title] = vo.title
            it[Posts.id] = id.value
        }
        vo.copy(id = id, createdAt = created)
    }

    fun update(
        postId: Post.Id,
        authorId: User.Id? = null,
        categoryId: Category.Id? = null,
        status: Status? = null,
        title: String? = null
    ): Post.Raw = transaction {
        Posts.update {
            Posts.id eq postId.value
            authorId?.let { id -> it[Posts.authorId] = id.value }
            categoryId?.let { id -> it[Posts.categoryId] = id.value }
            status?.let { newStatus -> it[Posts.status] = newStatus }
            title?.let { newTitle -> it[Posts.title] = newTitle }
        }
        findById(postId)!!
    }

    override fun update(vo: Post.Raw): Post.Raw = transaction {
        assert(vo.id != null)
        Posts.update {
            Posts.id eq vo.id!!.value
            it[authorId] = vo.authorId.value
            it[categoryId] = vo.categoryId?.value
            it[status] = vo.status
            it[title] = vo.title
        }
        vo
    }

    fun find(
        categoryId: Category.Id? = null,
        status: List<Status>? = null,
        offsetLimit: OffsetLimit? = null
    ): List<Post.Raw> = transaction {
        val query = Posts.selectAll()
        offsetLimit?.apply { query.limit(this.limit, this.offset) }
        categoryId?.apply { query.adjustWhere { Posts.categoryId eq categoryId.value } }
        status?.apply { status.forEach { query.adjustWhere { Posts.status eq it } } }
        query.map { relationObjectMapping(it) }
    }

    fun findJoining(
        categoryId: Category.Id? = null,
        status: List<Status>? = null,
        offsetLimit: OffsetLimit? = null,
        keywords: String? = null,
    ): List<Post.Joined> =
        transaction {
            val query = joiningQuery(offsetLimit)
            categoryId?.apply { query.adjustWhere { Posts.categoryId eq categoryId.value } }
            status?.apply { status.forEach { query.adjustWhere { Posts.status eq it } } }
            keywords?.apply { query.adjustWhere { Posts.title like "%$keywords%" } }
            query.map { joinedRelationObjectMapping(it) }
        }

    fun updateCategory(prevCategory: Category.Id, newCategory: Category.Id?): Int = transaction {
        Posts.update {
            categoryId eq prevCategory.value
            it[categoryId] = newCategory?.value
        }
    }
}
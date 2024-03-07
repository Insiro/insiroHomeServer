package me.insiro.home.server.testUtils

import me.insiro.home.server.post.entity.*
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction


object DBInserter {
    fun insertUser(user: User): User = transaction {
        val id = Users.insertAndGetId {
            it[name] = user.name
            it[password] = user.hashedPassword
            it[email] = user.email
            it[permission] = user.permission
            it[Users.createdAt] = user.createdAt
        }
        user.copy(id = User.Id(id))
    }

    fun insertCategory(category: Category): Category = transaction {
        val id = Categories.insertAndGetId { it[name] = category.name }
        category.copy(id = Category.Id(id))
    }

    fun insertComment(comment: Comment): Comment = transaction {
        val id = Comments.insertAndGetId {
            it[authorInfo] = comment.author
            it[content] = comment.content
            it[parentId] = comment.parentId?.value
            it[postId] = comment.postId.value
        }
        comment.copy(id = Comment.Id(id))
    }

    fun insertPost(post: Post): Post = transaction {
        val id = Posts.insertAndGetId {
            it[status] = post.status
            it[title] = post.title
            it[createdAt] = post.createdAt
            it[category] = post.categoryId.value
            it[authorId] = post.authorId.value
        }
        post.copy(id = Post.Id(id))
    }
}

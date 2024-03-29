package me.insiro.home.server.testUtils

import me.insiro.home.server.post.entity.*
import me.insiro.home.server.project.entity.*
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime


object DBInserter {
    fun insertUser(user: User): User = transaction {
        val id = Users.insertAndGetId {
            it[name] = user.name
            it[password] = user.hashedPassword
            it[email] = user.email
            it[permission] = user.permission
            it[Users.createdAt] = LocalDateTime.now()
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

    fun insertPost(post: Post.Raw): Post.Raw = transaction {
        val id = Posts.insertAndGetId {
            it[status] = post.status
            it[title] = post.title
            it[createdAt] = LocalDateTime.now()
            it[categoryId] = post.categoryId?.value
            it[authorId] = post.authorId.value
        }
        post.copy(id = Post.Id(id))
    }

    fun insertProject(project: Project.Raw): Project.Raw = transaction {
        val id = Projects.insertAndGetId {
            it[status] = project.status
            it[title] = project.title
        }
        project.copy(id = Project.Id(id))
    }

    fun insertProjectType(projectType: ProjectType): ProjectType = transaction {
        val id = ProjectTypes.insertAndGetId { it[name] = projectType.name }
        projectType.copy(id = ProjectType.Id(id))
    }

    fun insertProjectTypeRelation(project: Project, projectType: ProjectType) = transaction {
        ProjectTypeRelations.insert {
            it[projectId] = project.id!!.value
            it[typeId] = projectType.id!!.value
        }
    }
}

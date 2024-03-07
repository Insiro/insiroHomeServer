package me.insiro.home.server.post.service

import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.post.dto.comment.ModifyCommentDTO
import me.insiro.home.server.post.entity.*
import me.insiro.home.server.post.repository.CommentRepository
import me.insiro.home.server.testUtils.AbsDataBaseTest
import me.insiro.home.server.testUtils.DBInserter
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import me.insiro.home.server.user.utils.PasswordEncoder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommentServiceTest : AbsDataBaseTest(Users, Categories,Posts, Comments) {
    private val passwordEncoder = PasswordEncoder()
    private val commentRepository = CommentRepository()
    private val commentService = CommentService(commentRepository, passwordEncoder)
    private lateinit var user: User
    private lateinit var category: Category
    private lateinit var post: Post
    private lateinit var commentUserInfo: CommentUserInfo
    private lateinit var comment: Comment

    @BeforeEach
    fun resetTest() {
        resetDataBase()
        user = DBInserter.insertUser(User("testUser", "testPwd", "testEmail", 0b1, User.Id(1)))
        commentUserInfo = CommentUserInfo.UserInfo(user)
        category = DBInserter.insertCategory(Category("category"))
        post = DBInserter.insertPost(Post("testPost", Status.PUBLISHED, user.id!!, category.id!!))
        comment = DBInserter.insertComment(Comment("testComment", post.id!!, null, commentUserInfo))
    }

    @Test
    fun `find comment by Post Id`() {
        val comments = commentService.findComments(post.id!!)
        assertTrue(comments.isNotEmpty())
        assertEquals(listOf(comment), comments)
    }

    @Test
    fun deleteComment() {
        assertTrue(commentService.deleteComment(comment.id!!))
        val nComment = transaction {
            Comments.selectAll().where { Comments.id eq comment.id!!.value }.count()
        }
        assertEquals(0, nComment)
    }

    @Test
    fun getCommentById() {
        val found = commentService.getComment(comment.id!!)
        assertNotNull(found)
        assertEquals(comment.id, found!!.id)
        assertEquals(comment.createdAt, found.createdAt)
        assertEquals(comment.author, found.author)
        assertEquals(comment.postId, found.postId)
        assertEquals(comment.content, found.content)
        assertEquals(comment.parentId, found.parentId)
    }

    @Test
    fun updateComment() {
        val updateDTO = ModifyCommentDTO.Signed("commentUpdated")
        val wrongIdUpdated = commentService.updateComment(
            Comment.Id(comment.id!!.value + 1),
            updateDTO,
            user
        )
        assertNull(wrongIdUpdated)
        val updated = commentService.updateComment(comment.id!!, updateDTO, user)
        assertNotNull(updated)
        assertEquals(updateDTO.content, updated!!.content)
    }

    @Test
    fun `update Anonymous Comment`() {
        val pwd = "testPwd"
        val comment = DBInserter.insertComment(
            comment.copy(
                author = CommentUserInfo.Anonymous(
                    "testUser",
                    passwordEncoder.encode(pwd)
                )
            )
        )

        val updateDTO = ModifyCommentDTO.Anonymous("commentUpdated", "testUser", pwd)
        val updated = commentService.updateComment(comment.id!!, updateDTO)

        assertNotNull(updated)
        assertTrue(updated!!.author is CommentUserInfo.Anonymous)
        val updatedUser = updated.author as CommentUserInfo.Anonymous
        assertTrue(passwordEncoder.matches(pwd, updatedUser.pwd))
        assertEquals(updateDTO.name, updatedUser.name)
    }


    @Test
    fun `add comment with anonymous user`() {
        val pwd = "testPwd"
        val anonymousUser = CommentUserInfo.Anonymous("testUser", passwordEncoder.encode(pwd))
        val addDTO = ModifyCommentDTO.Anonymous("commentUpdated", "testUser", "testPwd")
        val added = commentService.addComment(post.id!!, addDTO)
        assertTrue(added.author is CommentUserInfo.Anonymous)
        val addedAuthor = added.author as CommentUserInfo.Anonymous
        assertEquals(addDTO.name, addedAuthor.name)
        assertTrue(passwordEncoder.matches(addDTO.password, addedAuthor.pwd))
        assertEquals(addDTO.content, added.content)
        assertEquals(post.id, added.postId)
    }

    @Test
    fun `add comment with signed user`() {
        val addDTO = ModifyCommentDTO.Signed("commentUpdated")
        val added = commentService.addComment(post.id!!, addDTO, user)

        assertTrue(added.author is CommentUserInfo.UserInfo)
        val addedAuthor = added.author as CommentUserInfo.UserInfo
        assertEquals(user.id, addedAuthor.id)
        assertEquals(user.name, addedAuthor.name)
        assertEquals(addDTO.content, added.content)
        assertEquals(post.id, added.postId)
    }

}
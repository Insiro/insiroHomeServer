package me.insiro.home.server.post.service

import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.post.dto.comment.ModifierDTO
import me.insiro.home.server.post.dto.comment.ModifyCommentDTO
import me.insiro.home.server.post.entity.*
import me.insiro.home.server.post.exception.comment.CommentNotFoundException
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
import org.junit.jupiter.api.assertThrows

class CommentServiceTest : AbsDataBaseTest(Users, Categories, Posts, Comments) {
    private val passwordEncoder = PasswordEncoder()
    private val commentRepository = CommentRepository()
    private val commentService = CommentService(commentRepository, passwordEncoder)
    private lateinit var user: User
    private lateinit var category: Category
    private lateinit var post: Post.Raw
    private lateinit var commentUserInfo: CommentUserInfo
    private lateinit var comment: Comment

    @BeforeEach
    fun resetTest() {
        resetDataBase()
        user = DBInserter.insertUser(User("testUser", "testPwd", "testEmail", 0b1, User.Id(1)))
        commentUserInfo = CommentUserInfo.UserInfo(user)
        category = DBInserter.insertCategory(Category("CATEGORY"))
        post = DBInserter.insertPost(Post.Raw("testPost", Status.PUBLISHED, user.id!!, category.id!!))
        comment = DBInserter.insertComment(Comment("testComment", post.id!!, null, commentUserInfo))
    }

    @Test
    fun `find comment by Post Id`() {
        val comments = commentService.findComments(post.id!!, null).map { it.copy(createdAt = null) }
        assertTrue(comments.isNotEmpty())
        assertEquals(listOf(comment), comments)
    }

    @Test
    fun deleteComment() {
        val modifier = ModifierDTO.Signed()
        assertTrue(commentService.deleteComment(comment.id!!, modifier, user).getOrThrow())
        val nComment = transaction {
            Comments.selectAll().where { Comments.id eq comment.id!!.value }.count()
        }
        assertEquals(0, nComment)
    }

    @Test
    fun getCommentById() {
        val found = commentService.getComment(comment.id!!).getOrThrow()
        assertEquals(comment.id, found.id)
        assertEquals(comment.author, found.author)
        assertEquals(comment.postId, found.postId)
        assertEquals(comment.content, found.content)
        assertEquals(comment.parentId, found.parentId)
    }

    @Test
    fun updateComment() {
        val updateDTO = ModifyCommentDTO("commentUpdated", ModifierDTO.Signed())
        val wrongIdUpdated = commentService.updateComment(
            Comment.Id(comment.id!!.value + 1),
            updateDTO,
            user
        )
        assertThrows<CommentNotFoundException> { wrongIdUpdated.getOrThrow() }
        val updated = commentService.updateComment(comment.id!!, updateDTO, user)
        assertNotNull(updated)
        assertEquals(updateDTO.content, updated.getOrNull()!!.content)
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


        val modifier = ModifierDTO.Anonymous("testUser", pwd)
        val updateDTO = ModifyCommentDTO("commentUpdated", modifier)
        val updated = commentService.updateComment(comment.id!!, updateDTO).getOrThrow()

        assertTrue(updated.author is CommentUserInfo.Anonymous)
        val updatedUser = updated.author as CommentUserInfo.Anonymous
        assertTrue(passwordEncoder.matches(pwd, updatedUser.pwd))
        assertEquals(modifier.name, updatedUser.name)
    }


    @Test
    fun `add comment with anonymous user`() {
        val modifier = ModifierDTO.Anonymous("testUser", "testPwd")
        val addDTO = ModifyCommentDTO("commentUpdated", modifier)
        val added = commentService.addComment(post.id!!, addDTO)

        //region Check User
        assertTrue(added.author is CommentUserInfo.Anonymous)
        val addedAuthor = added.author as CommentUserInfo.Anonymous
        assertEquals(modifier.name, addedAuthor.name)
        assertTrue(passwordEncoder.matches(modifier.password, addedAuthor.pwd))
        //endregion
        assertEquals(addDTO.content, added.content)
        assertEquals(post.id, added.postId)
    }

    @Test
    fun `add comment with signed user`() {
        val addDTO = ModifyCommentDTO("commentUpdated",ModifierDTO.Signed())
        val added = commentService.addComment(post.id!!, addDTO, user)

        assertTrue(added.author is CommentUserInfo.UserInfo)
        val addedAuthor = added.author as CommentUserInfo.UserInfo
        assertEquals(user.id, addedAuthor.id)
        assertEquals(user.name, addedAuthor.name)
        assertEquals(addDTO.content, added.content)
        assertEquals(post.id, added.postId)
    }

}
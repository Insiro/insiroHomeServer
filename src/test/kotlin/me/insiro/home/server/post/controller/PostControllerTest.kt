package me.insiro.home.server.post.controller

import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.post.dto.comment.ModifyCommentDTO
import me.insiro.home.server.post.dto.post.NewPostDTO
import me.insiro.home.server.post.dto.post.UpdatePostDTO
import me.insiro.home.server.post.entity.*
import me.insiro.home.server.post.service.CategoryService
import me.insiro.home.server.post.service.CommentService
import me.insiro.home.server.post.service.PostService
import me.insiro.home.server.testUtils.AbsControllerTest
import me.insiro.home.server.user.dto.AuthDetail
import me.insiro.home.server.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PostControllerTest : AbsControllerTest("/posts") {
    @Mock
    private val postService = mock(PostService::class.java)
    private val categoryService = mock(CategoryService::class.java)
    private val commentService = mock(CommentService::class.java)
    private val user = User("testUser", "", "testEmail", 0b1, id = User.Id(1), LocalDateTime.now())
    private val category = Category("Default", Category.Id(0), LocalDateTime.now())
    private val comment = Comment("", Post.Id(1), null, CommentUserInfo.UserInfo(user), Comment.Id(1), LocalDateTime.now())
    private val post = JoinedPost("testPost", Status.PUBLISHED, user, category, id = Post.Id(1), LocalDateTime.now())
    private val detail = AuthDetail(user)

    @BeforeEach
    override fun init() {
        val controller = PostController(postService, categoryService, commentService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun testGetPosts() {
        Mockito.`when`(postService.findPosts()).thenReturn(listOf())
        mockMvc.perform(MockMvcRequestBuilders.get(uri))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isArray }
    }

    @Test
    fun testGetPostById() {
        Mockito.`when`(postService.findPost(post.id!!)).thenReturn(post)
        Mockito.`when`(categoryService.findById(category.id!!)).thenReturn(category)
        Mockito.`when`(commentService.findComments(post.id!!)).thenReturn(listOf(comment))
        mockMvc.perform(MockMvcRequestBuilders.get(uri(post.id!!)).queryParam("comment", "true"))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.status").value(post.status) }
            .andExpect { jsonPath("$.category").value(category) }
            .andExpect { jsonPath("$.title").value(post.title) }
            .andExpect { jsonPath("$.author.id").value(post.author.id) }
            .andDo { println(it.response.contentAsString) }
        mockMvc.perform(MockMvcRequestBuilders.get(uri(post.id!!)))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.comments").isEmpty }

    }

    @Test
    fun testDeletePostById() {
        Mockito.`when`(postService.deletePost(post.id!!, user)).thenReturn(true)
        Mockito.`when`(commentService.deleteCommentByPostId(post.id!!)).thenReturn(1)
        mockMvc.perform(MockMvcRequestBuilders.delete(uri(post.id!!))
            .with {
                val authentication = UsernamePasswordAuthenticationToken(detail, "", detail.authorities)
                SecurityContextHolder.getContext().authentication = authentication
                it
            })
            .andExpect { status().isOk }
    }

    @Test
    fun updatePost() {
        val cate2 = Category("cate2", Category.Id(2))
        val updated = post.copy(category = cate2, status = Status.HIDDEN)
        val updateDTO = UpdatePostDTO(category = updated.category.name, status = updated.status)

        Mockito.`when`(postService.updatePost(post.id!!, updateDTO, cate2.id, user)).thenReturn(updated)
        Mockito.`when`(categoryService.findByName(cate2.name)).thenReturn(cate2)

        mockMvc.perform(
            MockMvcRequestBuilders.patch(uri(post.id!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(updateDTO))
                .with {
                    val authentication = UsernamePasswordAuthenticationToken(detail, "", detail.authorities)
                    SecurityContextHolder.getContext().authentication = authentication
                    it
                }
        )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.status").value(updated.status) }
            .andExpect { jsonPath("$.category").value(cate2) }
            .andExpect { jsonPath("$.title").value(updated.title) }
            .andExpect { jsonPath("$.author.id").value(updated.author.id) }
            .andExpect { jsonPath("$.comments").isArray }
    }

    @Test
    fun testCreatePost() {
        val createDTO = NewPostDTO(post.title, category.name, "New Post Content")
        Mockito.`when`(categoryService.findByName(category.name)).thenReturn(category)
        Mockito.`when`(postService.createPost(createDTO, category.id, user)).thenReturn(post)

        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(createDTO))
                .with {
                    val authentication = UsernamePasswordAuthenticationToken(detail, "", detail.authorities)
                    SecurityContextHolder.getContext().authentication = authentication
                    it
                }
        )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.status").value(post.status) }
            .andExpect { jsonPath("$.category").value(post.category) }
            .andExpect { jsonPath("$.title").value(post.title) }
            .andExpect { jsonPath("$.author.id").value(post.author.id) }
            .andExpect { jsonPath("$.comments").isArray }
    }

    @Test
    fun `test add comment with signed user`() {
        val signedDTO = ModifyCommentDTO.Signed("content")
        Mockito.`when`(postService.findPost(post.id!!)).thenReturn(post)
        Mockito.`when`(commentService.addComment(post.id!!, signedDTO, user)).thenReturn(comment)
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri(post.id!!, "comments", "signed"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(signedDTO))
                .with {
                    val authentication = UsernamePasswordAuthenticationToken(detail, "", detail.authorities)
                    SecurityContextHolder.getContext().authentication = authentication
                    it
                }
        )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.author").value(comment.author) }
            .andExpect { jsonPath("$.content").value(comment.content) }
            .andExpect { jsonPath("$.postId").value(comment.postId) }
            .andExpect { jsonPath("$.parentId").value(comment.parentId) }
    }

    @Test
    fun `test add comment with anonymous user`() {
        val anonymousDTO = ModifyCommentDTO.Anonymous("content", "testAnonymous", "testPwd")
        val anonyComment = Comment(
            anonymousDTO.content,
            post.id!!,
            null,
            CommentUserInfo.Anonymous(anonymousDTO.name, anonymousDTO.password),
            id = Comment.Id(2)
        )
        Mockito.`when`(postService.findPost(post.id!!)).thenReturn(post)
        Mockito.`when`(commentService.addComment(post.id!!, anonymousDTO)).thenReturn(anonyComment)
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri(post.id!!, "comments"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(anonymousDTO))
        )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.author.id").value(post.author.id) }
    }

    @Test
    fun testGetComments() {
        Mockito.`when`(commentService.findComments(post.id!!)).thenReturn(listOf(comment))

        mockMvc.perform(MockMvcRequestBuilders.get(uri(post.id!!, "comments")).contentType(MediaType.APPLICATION_JSON))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.comments").value(listOf(comment)) }
    }
}



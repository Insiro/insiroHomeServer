package me.insiro.home.server.post.controller

import me.insiro.home.server.application.domain.Status
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

@ExtendWith(MockitoExtension::class)
class PostControllerTest : AbsControllerTest("/posts") {
    @Mock
    private val postService = mock(PostService::class.java)
    private val categoryService = mock(CategoryService::class.java)
    private val commentService = mock(CommentService::class.java)
    private val user = User("testUser", "", "testEmail", 0b1, id = User.Id(1))
    private val category = Category("Default", Category.Id(0))
    private val comment = Comment("", Post.Id(1), null, CommentUserInfo.UserInfo(user), Comment.Id(1))
    private val post = JoinedPost("testPost", Status.PUBLISHED, user, category, id = Post.Id(1))
    private lateinit var controller: PostController
    private val detail = AuthDetail(user)

    @BeforeEach
    override fun init() {
        controller = PostController(postService, categoryService, commentService)
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
        Mockito.`when`(categoryService.get(category.id!!)).thenReturn(category)
        Mockito.`when`(commentService.findComments(post.id!!)).thenReturn(listOf(comment))
        mockMvc.perform(MockMvcRequestBuilders.get(uri(post.id)).queryParam("comment", "true"))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.status").value(post.status) }
            .andExpect { jsonPath("$.category").value(category) }
            .andExpect { jsonPath("$.title").value(post.title) }
            .andExpect { jsonPath("$.author.id").value(post.author.id) }
            .andDo { println(it.response.contentAsString) }
        mockMvc.perform(MockMvcRequestBuilders.get(uri(post.id)))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.comments").isEmpty }

    }

    @Test
    fun testDeletePostById() {
        Mockito.`when`(postService.deletePost(post.id!!, user)).thenReturn(true)
        Mockito.`when`(commentService.deleteComment(post.id!!)).thenReturn(1)
        mockMvc.perform(MockMvcRequestBuilders.delete(uri(post.id))
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
        Mockito.`when`(categoryService.get(cate2.name)).thenReturn(cate2)

        mockMvc.perform(
            MockMvcRequestBuilders.patch(uri(post.id))
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
        Mockito.`when`(categoryService.get(category.name)).thenReturn(category)
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

}



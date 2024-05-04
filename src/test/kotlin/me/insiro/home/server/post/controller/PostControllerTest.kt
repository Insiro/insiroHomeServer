package me.insiro.home.server.post.controller

import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.file.service.PostFileService
import me.insiro.home.server.post.dto.comment.ModifierDTO
import me.insiro.home.server.post.dto.comment.ModifyCommentDTO
import me.insiro.home.server.post.dto.post.NewPostDTO
import me.insiro.home.server.post.dto.post.UpdatePostDTO
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.CommentUserInfo
import me.insiro.home.server.post.entity.Post
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
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.web.MockPart
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class PostControllerTest : AbsControllerTest("/posts") {
    @Mock
    private val postService = mock(PostService::class.java)
    private val categoryService = mock(CategoryService::class.java)
    private val commentService = mock(CommentService::class.java)
    private val postFileService = mock(PostFileService::class.java)
    private val user = User("testUser", "", "testEmail", 0b1, id = User.Id(1), LocalDateTime.now())
    private val category = Category("DEFAULT", Category.Id(0), LocalDateTime.now())
    private val post =
        Post.Raw(
            "testPost",
            Status.PUBLISHED,
            user.id!!,
            category.id,
            id = Post.Id(UUID.randomUUID()),
            LocalDateTime.now()
        )
    private val comment =
        Comment("", post.id!!, null, CommentUserInfo.UserInfo(user), Comment.Id(1), LocalDateTime.now())

    private val joinedPost = Post.Joined(
        "testPost", Status.PUBLISHED,
        Post.Joined.AuthorInfo(user.id!!, user.name), category, id = post.id!!, LocalDateTime.now()
    )
    private val detail = AuthDetail(user)

    @BeforeEach
    override fun init() {
        val controller = PostController(postService, categoryService, postFileService, commentService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun testGetPosts() {
        Mockito.`when`(postService.findJoinedPosts(null, arrayListOf(Status.PUBLISHED), null)).thenReturn(listOf())
        mockMvc.perform(MockMvcRequestBuilders.get(uri))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isArray }
    }

    @Test
    fun testGetPostById() {
        Mockito.`when`(postService.findJoinedPost(post.id!!)).thenReturn(Result.success(joinedPost))
        Mockito.`when`(categoryService.findById(category.id!!)).thenReturn(Result.success(category))
        Mockito.`when`(commentService.findComments(post.id!!, null)).thenReturn(listOf(comment))
        mockMvc.perform(MockMvcRequestBuilders.get(uri(post.id!!)).queryParam("comment", "true"))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.status").value(post.status) }
            .andExpect { jsonPath("$.category").value(category) }
            .andExpect { jsonPath("$.title").value(post.title) }
            .andExpect { jsonPath("$.author.id").value(post.authorId) }
            .andDo { println(it.response.contentAsString) }
        mockMvc.perform(MockMvcRequestBuilders.get(uri(post.id!!)))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.comments").isEmpty }

    }

    @Test
    fun testDeletePostById() {
        Mockito.`when`(postService.findPost(post.id!!)).thenReturn(Result.success(post))
        Mockito.`when`(postService.deletePost(post.id!!, detail.user)).thenReturn(Result.success(true))
        Mockito.`when`(commentService.deleteCommentByPostId(post.id!!)).thenReturn(1)
        mockMvc.perform(MockMvcRequestBuilders.delete(uri(post.id!!))
            .apply {
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(detail, "", detail.authorities)
            })
            .andExpect { status().isOk }
    }

    @Test
    fun updatePost() {
        val cate2 = Category("cate2", Category.Id(2), createdAt = LocalDateTime.now())
        val updated = post.copy(categoryId = cate2.id, status = Status.HIDDEN)
        val updateDTO = UpdatePostDTO(category = cate2.name, status = updated.status)
        val dtoPart = MockPart("data", gson.toJson(updateDTO).toByteArray())
        dtoPart.headers.contentType = MediaType.APPLICATION_JSON
        Mockito.`when`(postService.updatePost(post.id!!, updateDTO, cate2.id, user)).thenReturn(Result.success(updated))
        Mockito.`when`(categoryService.findByName(cate2.name)).thenReturn(Result.success(cate2))

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, uri(post.id!!))
                .part(dtoPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .apply {
                    SecurityContextHolder.getContext().authentication =
                        UsernamePasswordAuthenticationToken(detail, "", detail.authorities)
                }
        ).andExpect { status().isOk }
            .andExpect { jsonPath("$.status").value(updated.status) }
            .andExpect { jsonPath("$.category").value(cate2) }
            .andExpect { jsonPath("$.title").value(updated.title) }
            .andExpect { jsonPath("$.author.id").value(updated.authorId) }
            .andExpect { jsonPath("$.comments").isArray }
    }

    @Test
    fun testCreatePost() {
        val createDTO = NewPostDTO(post.title, category.name, "New Post Content", Status.PUBLISHED)
        Mockito.`when`(categoryService.findByName(category.name)).thenReturn(Result.success(category))
        Mockito.`when`(postService.createPost(createDTO, user, category.id)).thenReturn(Result.success(post))
        val dtoPart = MockPart("data", gson.toJson(createDTO).toByteArray())
        dtoPart.headers.contentType = MediaType.APPLICATION_JSON
        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart(uri)
                .part(dtoPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .apply {
                    SecurityContextHolder.getContext().authentication =
                        UsernamePasswordAuthenticationToken(detail, "", detail.authorities)
                }
        )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.status").value(post.status) }
            .andExpect { jsonPath("$.category").value(category) }
            .andExpect { jsonPath("$.title").value(post.title) }
            .andExpect { jsonPath("$.author.id").value(post.authorId) }
            .andExpect { jsonPath("$.comments").isArray }
    }

    @Test
    fun `test add comment with signed user`() {
        val signedDTO = ModifyCommentDTO("content", ModifierDTO.Signed())
        Mockito.`when`(commentService.addComment(post.id!!, signedDTO, user)).thenReturn(comment)
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri(post.id!!, "comments"))
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
        val modifierDTO = ModifierDTO.Anonymous("testAnonymous", "testPwd")
        val anonymousDTO = ModifyCommentDTO("content", modifierDTO)
        val anonymousComment = Comment(
            anonymousDTO.content,
            post.id!!,
            null,
            CommentUserInfo.Anonymous(modifierDTO.name, modifierDTO.password),
            createdAt = LocalDateTime.now(),
            id = Comment.Id(2)
        )
        Mockito.`when`(commentService.addComment(post.id!!, anonymousDTO)).thenReturn(anonymousComment)
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri(post.id!!, "comments"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(anonymousDTO))
        )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.author.id").value(post.authorId) }
    }

    @Test
    fun testGetComments() {
        Mockito.`when`(commentService.findComments(post.id!!, null)).thenReturn(listOf(comment))

        mockMvc.perform(MockMvcRequestBuilders.get(uri(post.id!!, "comments")).contentType(MediaType.APPLICATION_JSON))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.comments").value(listOf(comment)) }
    }
}



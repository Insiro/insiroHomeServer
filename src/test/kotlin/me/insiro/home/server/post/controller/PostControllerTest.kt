package me.insiro.home.server.post.controller

import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.post.dto.post.NewPostDTO
import me.insiro.home.server.post.dto.post.UpdatePostDTO
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.CommentUserInfo
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.testUtils.AbsControllerTest
import me.insiro.home.server.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.stereotype.Service
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ExtendWith(MockitoExtension::class)
class PostControllerTest : AbsControllerTest("posts") {
    @Mock
    private val postService = mock(PostService::class.java)
    private val categoryService = mock(CategoryService::class.java)
    private val commentService = mock(CommentService::class.java)
    private val user = User("testUser", "", "testEmail", 0b1, id = User.Id(1))
    private val category = Category("Default", Category.Id(0))
    private val comment = Comment("", Post.Id(1), null, CommentUserInfo.UserInfo(user.id!!), Comment.Id(1))
    private val post =
        Post("testPost", Status.PUBLISHED, user.id!!, category.id!!, id = Post.Id(1), comments = arrayListOf(comment))

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
        val noComment = post.copy(comments = arrayListOf())
        Mockito.`when`(postService.findPost(post.id!!)).thenReturn(noComment)
        Mockito.`when`(commentService.findByPostId(post.id!!)).thenReturn(listOf())
        Mockito.`when`(categoryService.find(post.category)).thenReturn(category)
        mockMvc.perform(MockMvcRequestBuilders.get(uri(post.id)).queryParam("comment", "true"))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.status").value(post.status) }
            .andExpect { jsonPath("$.category").value(category) }
            .andExpect { jsonPath("$.title").value(post.title) }
            .andExpect { jsonPath("$.author.id").value(post.authorId) }
            .andExpect { jsonPath("$.comments").isArray }
        mockMvc.perform(MockMvcRequestBuilders.get(uri(post.id)))
            .andExpect { jsonPath("$.comments").isEmpty() }
    }

    @Test
    fun testDeletePostById() {
        Mockito.`when`(postService.deletePost(post.id!!)).thenReturn(true)
        mockMvc.perform(MockMvcRequestBuilders.delete(uri(post.id)))
            .andExpect { status().isOk }
    }

    @Test
    fun updatePost() {
        val cate2 = Category("cate2", Category.Id(2))
        val updateDTO = UpdatePostDTO(category = cate2.name, status = Status.HIDDEN)
        Mockito.`when`(postService.updatePost(post.id!!, updateDTO)).thenReturn(post)
        Mockito.`when`(categoryService.get(cate2.name)).thenReturn(cate2)

        mockMvc.perform(MockMvcRequestBuilders.patch(uri(post.id)))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.status").value(updateDTO.status) }
            .andExpect { jsonPath("$.category").value(cate2) }
            .andExpect { jsonPath("$.title").value(post.title) }
            .andExpect { jsonPath("$.author.id").value(post.authorId) }
            .andExpect { jsonPath("$.comments").isArray }
    }

    @Test
    fun createPost() {
        val createDTO = NewPostDTO(post.title, category.name, "New Post Content", post.status)
        Mockito.`when`(postService.createPost(createDTO)).thenReturn(post)
        Mockito.`when`(categoryService.get(category.name)).thenReturn(category)

        mockMvc.perform(MockMvcRequestBuilders.patch(uri(post.id)))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.status").value(post.status) }
            .andExpect { jsonPath("$.category").value(category) }
            .andExpect { jsonPath("$.title").value(post.title) }
            .andExpect { jsonPath("$.author.id").value(post.authorId) }
            .andExpect { jsonPath("$.comments").isArray }
    }

}

@RestController
@RequestMapping("comment")
class PostController(
    private val postService: PostService,
    private val categoryService: CategoryService,
    val commentService: CommentService,
)
@Service
class PostService {
    fun createPost(createDTO: NewPostDTO): Post {
        TODO("Not Yet Implemented")
    }

    fun updatePost(id: Post.Id, updateDTO: UpdatePostDTO): Post {
        TODO("Not Yet Implemented")
    }

    fun deletePost(id: Post.Id): Boolean {
        TODO("Not Yet Implemented")
    }

    fun findPost(id: Post.Id): Post? {
        TODO("Not Yet Implemented")
    }

    fun findPosts(): List<Post> {
        TODO("Not Yet Implemented")
    }
}

@Service
class CategoryService {
    fun get(name: String): Category {
        TODO("Not Yet Implemented")

    }

    fun find(category: Category.Id): Category? {
        TODO("Not Yet Implemented")
    }
}

@Service
class CommentService {
    fun findByPostId(id: Post.Id): List<Comment> {
        TODO("Not Yet Implemented")
    }
}
package me.insiro.home.server.post.controller

import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.post.dto.category.ModifyCategoryDTO
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.post.service.CategoryService
import me.insiro.home.server.post.service.PostService
import me.insiro.home.server.testUtils.AbsControllerTest
import me.insiro.home.server.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

class CategoryControllerTest : AbsControllerTest("/category") {
    private val postService = mock(PostService::class.java)
    private val categoryService = mock(CategoryService::class.java)
    private val category = Category("TEST_CATEGORY", Category.Id(1))

    @BeforeEach
    override fun init() {
        val controller = CategoryController(categoryService, postService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `create Category`() {
        val dto = ModifyCategoryDTO(category.name)
        Mockito.`when`(categoryService.create(dto)).thenReturn(category)
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .content(gson.toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect { status().isCreated }
            .andExpect { jsonPath("$.name").value(dto.name) }
    }

    @Test
    fun `update Category`() {
        val dto = ModifyCategoryDTO(category.name)
        Mockito.`when`(categoryService.update(category.id!!, dto)).thenReturn(Result.success(category))
        mockMvc.perform(
            MockMvcRequestBuilders.patch(uri(category.id!!))
                .content(gson.toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.id").value(category.id!!.value) }
            .andExpect { jsonPath("$.name").value(category.name) }
    }

    @Test
    fun `get Category Info`() {
        Mockito.`when`(categoryService.findByName(category.name)).thenReturn(Result.success(category))
        mockMvc.perform(MockMvcRequestBuilders.get(uri(category.name)))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.name").value(category.name) }
            .andExpect { jsonPath("$.id").value(category.id!!.value) }
    }

    @Test
    fun `get Categories Info`() {
        Mockito.`when`(categoryService.findAll(null)).thenReturn(listOf(category))
        mockMvc.perform(MockMvcRequestBuilders.get(uri))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isArray }
    }


    @Test
    fun `get posts By category`() {
        val posts = listOf(
            Post.Joined(
                "testPost",
                Status.PUBLISHED,
                Post.Joined.AuthorInfo(User.Id(1), "testUser"),
                category,
                Post.Id(UUID.randomUUID())
            )
        )
        Mockito.`when`(categoryService.findByName(category.name)).thenReturn(Result.success(category))
        Mockito.`when`(postService.findJoinedPosts(category.id!!)).thenReturn(posts)
        mockMvc.perform(MockMvcRequestBuilders.post(uri(category.id!!, "posts")))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isArray }
            .andExpect { jsonPath("$[0].category.name").value(category.name) }
    }

    @Test
    fun `delete category`() {
        Mockito.`when`(categoryService.delete(category.name)).thenReturn(Result.success(category.id!!))
        mockMvc.perform(MockMvcRequestBuilders.delete(uri(category.id!!)))
            .andExpect { status().isOk }
    }
}


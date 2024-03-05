package me.insiro.home.server.post.controller

import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.post.dto.category.CategoryDTO
import me.insiro.home.server.post.dto.category.ModifyCategoryDTO
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.entity.JoinedPost
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.post.exception.CategoryNotFoundException
import me.insiro.home.server.post.service.CategoryService
import me.insiro.home.server.post.service.PostService
import me.insiro.home.server.testUtils.AbsControllerTest
import me.insiro.home.server.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.*

class CategoryControllerTest : AbsControllerTest("/category") {
    private val postService = mock(PostService::class.java)
    private val categoryService = mock(CategoryService::class.java)
    private val category = Category("testCategory", Category.Id(1))

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
        Mockito.`when`(categoryService.update(category.id!!, dto)).thenReturn(category)
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
        Mockito.`when`(categoryService.findByName(category.name)).thenReturn(category)
        mockMvc.perform(MockMvcRequestBuilders.get(uri(category.name)))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.name").value(category.name) }
            .andExpect { jsonPath("$.id").value(category.id!!.value) }
    }

    @Test
    fun `get Categories Info`() {
        Mockito.`when`(categoryService.findAll()).thenReturn(listOf(category))
        mockMvc.perform(MockMvcRequestBuilders.get(uri))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isArray }
    }


    @Test
    fun `get posts By category`() {
        val posts = listOf(JoinedPost("testPost", Status.PUBLISHED, User("", "", "", 0b1), category, Post.Id(1)))
        Mockito.`when`(categoryService.findByName(category.name)).thenReturn(category)
        Mockito.`when`(postService.findPostByCategory(category.id!!)).thenReturn(posts)
        mockMvc.perform(MockMvcRequestBuilders.post(uri(category.id!!, "posts")))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isArray }
            .andExpect { jsonPath("$[0].category.name").value(category.name) }
    }

    @Test
    fun `delete category`() {
        Mockito.`when`(categoryService.delete(category.name)).thenReturn(category.id)
        mockMvc.perform(MockMvcRequestBuilders.delete(uri(category.id!!)))
            .andExpect { status().isOk }
    }
}

@RestController
@RequestMapping("categories")
class CategoryController(
    private val categoryService: CategoryService,
    private val postService: PostService,
) {
    @GetMapping
    fun getAllCategories(): ResponseEntity<List<CategoryDTO>> {
        val categories = categoryService.findAll().map(::CategoryDTO)
        return ResponseEntity(categories, HttpStatus.OK)
    }

    @PostMapping
    fun createCategory(@RequestBody newCategoryDTO: ModifyCategoryDTO): ResponseEntity<CategoryDTO> {
        val category = categoryService.create(newCategoryDTO) ?: throw CategoryConflictException(newCategoryDTO.name)
        return ResponseEntity(CategoryDTO(category), HttpStatus.CREATED)
    }

    @GetMapping("{name}")
    fun getCategory(@PathVariable name: String): ResponseEntity<CategoryDTO> {
        val category = categoryService.findByName(name) ?: throw CategoryNotFoundException(name)
        return ResponseEntity(CategoryDTO(category), HttpStatus.OK)
    }

    @PatchMapping("{name}")
    fun updateCategory(
        @PathVariable name: String,
        @RequestBody modifyDTO: ModifyCategoryDTO
    ): ResponseEntity<CategoryDTO> {
        val category = categoryService.update(name, modifyDTO) ?: throw CategoryNotFoundException(name)
        return ResponseEntity(CategoryDTO(category), HttpStatus.OK)
    }

    @DeleteMapping("{name}")
    fun deleteCategory(@PathVariable name: String): ResponseEntity<String> {
        val categoryId = categoryService.delete(name) ?: throw CategoryNotFoundException(name)
        postService.changeCategory(categoryId, null)
        return ResponseEntity("success", HttpStatus.OK)
    }

}

class CategoryConflictException : AbsException {
    constructor(name: String) : super(HttpStatus.CONFLICT, "Category is Conflicted (name : $name)")
    constructor(id: Category.Id) : super(HttpStatus.CONFLICT, "Category is Conflicted (id : $id)")
}
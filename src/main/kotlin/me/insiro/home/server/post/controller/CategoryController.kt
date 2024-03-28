package me.insiro.home.server.post.controller

import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.post.dto.category.CategoryDTO
import me.insiro.home.server.post.dto.category.ModifyCategoryDTO
import me.insiro.home.server.post.service.CategoryService
import me.insiro.home.server.post.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("categories")
class CategoryController(
    private val categoryService: CategoryService,
    private val postService: PostService,
) {
    @GetMapping
    fun getAllCategories(
        @RequestParam(required = false) offset: Long = 0,
        @RequestParam(required = false) limit: Int? = null
    ): ResponseEntity<List<CategoryDTO>> {
        val offsetLimit = limit?.let { OffsetLimit(offset, limit) }
        val categories = categoryService.findAll(offsetLimit).map(::CategoryDTO)
        return ResponseEntity(categories, HttpStatus.OK)
    }

    @Secured("ROLE_WRITER")
    @PostMapping
    fun createCategory(@RequestBody newCategoryDTO: ModifyCategoryDTO): ResponseEntity<CategoryDTO> {
        val dto = categoryService.create(newCategoryDTO).let(::CategoryDTO)
        return ResponseEntity(dto, HttpStatus.CREATED)
    }

    @GetMapping("{name}")
    fun getCategory(@PathVariable name: String): ResponseEntity<CategoryDTO> {
        val category = categoryService.findByName(name).getOrThrow()
        return ResponseEntity(CategoryDTO(category), HttpStatus.OK)
    }

    @Secured("ROLE_WRITER")
    @PatchMapping("{name}")
    fun updateCategory(
        @PathVariable name: String,
        @RequestBody modifyDTO: ModifyCategoryDTO
    ): ResponseEntity<CategoryDTO> {
        val dto = categoryService.update(name, modifyDTO).getOrThrow().let(::CategoryDTO)
        return ResponseEntity(dto, HttpStatus.OK)
    }

    @Secured("ROLE_WRITER")
    @DeleteMapping("{name}")
    fun deleteCategory(@PathVariable name: String): ResponseEntity<String> {
        val categoryId = categoryService.delete(name).getOrThrow()
        postService.changeCategoryOfPosts(categoryId, null)
        return ResponseEntity("success", HttpStatus.OK)
    }

}
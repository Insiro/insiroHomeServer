package me.insiro.home.server.post.controller

import me.insiro.home.server.post.dto.category.CategoryDTO
import me.insiro.home.server.post.dto.category.ModifyCategoryDTO
import me.insiro.home.server.post.exception.CategoryConflictException
import me.insiro.home.server.post.exception.CategoryNotFoundException
import me.insiro.home.server.post.service.CategoryService
import me.insiro.home.server.post.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
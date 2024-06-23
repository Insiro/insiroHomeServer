package me.insiro.home.server.post.service

import me.insiro.home.server.post.dto.category.ModifyCategoryDTO
import me.insiro.home.server.post.entity.Categories
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.exception.category.CategoryConflictException
import me.insiro.home.server.post.exception.category.CategoryNotFoundException
import me.insiro.home.server.post.repository.CategoryRepository
import me.insiro.home.server.testUtils.AbsDataBaseTest
import me.insiro.home.server.testUtils.DBInserter
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class CategoryServiceTest : AbsDataBaseTest(Categories) {
    private val categoryRepository = CategoryRepository()
    private val categoryService = CategoryService(categoryRepository)
    private lateinit var category: Category

    @BeforeEach
    fun resetTest() {
        resetDataBase()
        category = insert(Category("TEST_CATEGORY"))
    }

    private fun insert(category: Category): Category = transaction {
        DBInserter.insertCategory(category)
    }

    @Test
    fun findByName() {
        val cate = categoryService.findByName(category.name).getOrThrow()
        assertEquals(category.name, cate.name)
        assertEquals(category.id, cate.id)
        //test wrong name
        assertThrows<CategoryNotFoundException> {  categoryService.findByName("newName_${category.name}").getOrThrow()}
    }

    @Test
    fun findById() {
        val cate = categoryService.findById(category.id!!).getOrThrow()
        assertEquals(category.name, cate.name)
        assertEquals(category.id, cate.id)
        //test wrong name
        assertThrows<CategoryNotFoundException> {
            categoryService.findById(Category.Id(category.id!!.value + 1)).getOrThrow()
        }
    }

    @Test
    fun `test delete and get Id must return null`() {
        val id = categoryService.delete(category.name).getOrThrow()
        assertEquals(category.id, id)
        val category = categoryService.findById(id)
        assertThrows<CategoryNotFoundException> { category.getOrThrow() }
    }

    @Test
    fun create() {
        // Fail Test (Conflict)
        val dtoToFail = ModifyCategoryDTO(category.name)
        assertThrows<CategoryConflictException> { categoryService.create(dtoToFail) }

        // Success Test
        val dtoToSuccess = dtoToFail.copy("new${dtoToFail.name}")
        val created2 = categoryService.create(dtoToSuccess)
        assertNotNull(created2)
        assertEquals(dtoToSuccess.name, created2.name)
    }

    @Test
    fun update() {
        val updateDTO = ModifyCategoryDTO(category.name + "1")
        val updated = categoryService.update(Category.Id(category.id!!.value), updateDTO).getOrThrow()
        assertNotNull(updated)
        assertEquals(updateDTO.name, updated.name)
    }

    @Test
    fun `test update Failure`() {
        // Not Found
        val notFoundDTO = ModifyCategoryDTO(category.name + "1")
        val updated = categoryService.update(Category.Id(category.id!!.value + 1), notFoundDTO)
        assertThrows<CategoryNotFoundException> { updated.getOrThrow() }

        insert(category.copy(name = category.name + "2"))
        val conflictDTO = ModifyCategoryDTO(category.name + "2")
        assertThrows<CategoryConflictException> { categoryService.update(category.id!!, conflictDTO) }

    }


    @Test
    fun findAll() {
        val categories = categoryService.findAll(null)
        assertEquals(listOf(category), categories)
    }
}


package me.insiro.home.server.post.service

import me.insiro.home.server.post.dto.category.ModifyCategoryDTO
import me.insiro.home.server.post.entity.Categories
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.exception.category.CategoryConflictException
import me.insiro.home.server.post.repository.CategoryRepository
import me.insiro.home.server.testUtils.AbsDataBaseTest
import me.insiro.home.server.testUtils.DBInserter
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
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
        category = insert(Category("testCategory"))
    }

    private fun insert(category: Category): Category = transaction {
        DBInserter.insertCategory(category)
    }

    @Test
    fun findByName() {
        val cate = categoryService.findByName(category.name)
        assertNotNull(cate)
        assertEquals(category.name, cate!!.name)
        assertEquals(category.id, cate.id)
        //test wrong name
        assertNull(categoryService.findByName("newName_${category.name}"))
    }

    @Test
    fun findById() {
        val cate = categoryService.findById(category.id!!)
        assertNotNull(cate)
        assertEquals(category.name, cate!!.name)
        assertEquals(category.id, cate.id)
        assertNotNull(cate.createdAt)
        //test wrong name
        assertNull(categoryService.findById(Category.Id(category.id!!.value + 1)))
    }

    @Test
    fun `test delete and get Id must return null`() {
        val id = categoryService.delete(category.name)
        assertEquals(category.id, id)
        val category = categoryService.findById(id!!)
        assertNull(category)
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
        assertEquals(dtoToSuccess.name, created2!!.name)
    }

    @Test
    fun update() {
        val updateDTO = ModifyCategoryDTO(category.name + "1")
        val updated = categoryService.update(Category.Id(category.id!!.value), updateDTO)
        assertNotNull(updated)
        assertEquals(updateDTO.name, updated!!.name)
    }

    @Test
    fun `test update Failure`() {
        // Not Found
        val notFoundDTO = ModifyCategoryDTO(category.name + "1")
        val updated = categoryService.update(Category.Id(category.id!!.value + 1), notFoundDTO)
        assertNull(updated)
        insert(category.copy(name = category.name + "2"))
        val conflictDTO = ModifyCategoryDTO(category.name + "2")
        assertThrows<CategoryConflictException> { categoryService.update(category.id!!, conflictDTO) }

    }


    @Test
    fun findAll() {
        val categories = categoryService.findAll(null).map { it.copy(createdAt = null) }
        assertEquals(listOf(category), categories)
    }
}


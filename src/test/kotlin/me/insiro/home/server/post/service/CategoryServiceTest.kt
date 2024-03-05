package me.insiro.home.server.post.service

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.post.dto.category.ModifyCategoryDTO
import me.insiro.home.server.post.entity.Categories
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.testUtils.AbsDataBaseTest
import net.bytebuddy.utility.RandomString
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus


class CategoryServiceTest : AbsDataBaseTest(listOf(Categories)) {
    private val categoryRepository = CategoryRepository()
    private val categoryService = CategoryService(categoryRepository)
    private lateinit var category: Category

    @BeforeEach
    fun resetTest() {
        resetDataBase()
        category = insert(Category("testCategory"))
    }

    private fun insert(category: Category): Category = transaction {
        val id = Categories.insertAndGetId { it[name] = category.name }.value
        category.copy(id = Category.Id(id))
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
        val created = categoryService.create(dtoToFail)
        assertNull(created)
        // Test Fail (Too long name)
        assertThrows<CategoryWriteFailedException> {
            ModifyCategoryDTO(category.name + RandomString.make(50))
        }

        // Success Test
        val dtoToSuccess = dtoToFail.copy("new${dtoToFail.name}")
        val created2 = categoryService.create(dtoToSuccess)
        assertNotNull(created2)
        assertEquals(dtoToSuccess.name, created2!!.name)
    }

    @Test
    fun update() {
        // Not Found
        val updateDTO = ModifyCategoryDTO(category.name + "1")
        var updated = categoryService.update(Category.Id(category.id!!.value + 1), updateDTO)
        assertNull(updated)
        // Test Fail (Too long name)
        assertThrows<CategoryWriteFailedException> {
            ModifyCategoryDTO(category.name + RandomString.make(50))
        }
        // Update Success
        updated = categoryService.update(Category.Id(category.id!!.value), updateDTO)
        assertNotNull(updated)
        assertEquals(category.name, updated!!.name)

    }

    @Test
    fun findAll() {
        val categories = categoryService.findAll()
        assertEquals(listOf(category), categories)
    }
}

class CategoryRepository : AbsRepository<Int, Categories, Category, Category.Id> {
    override val table: Categories
        get() = TODO("Not yet implemented")

    override fun relationObjectMapping(it: ResultRow): Category {
        TODO("Not yet implemented")
    }

    override fun update(vo: Category): Category {
        TODO("Not yet implemented")
    }

    override fun new(vo: Category): Category {
        TODO("Not yet implemented")
    }

}

class CategoryWriteFailedException(category: Category) : AbsException(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "failed write Category\n$category"
)
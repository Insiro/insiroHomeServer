package me.insiro.home.server.application

import me.insiro.home.server.application.domain.EntityVO
import me.insiro.home.server.testUtils.AbsDataBaseTest
import org.jetbrains.exposed.dao.DaoEntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)

class AbsRepositoryTest : AbsDataBaseTest(listOf(TestEntities)) {
    object TestEntities : IntIdTable() {
        val value = integer("value")
    }

    // Exposed DAO Pattern
    class TestEntity(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<TestEntity>(TestEntities)

        var value by TestEntities.value
    }

    data class TestVO(
            var value: Int,
    ) : EntityVO<Int>()


    class TestRepository : AbsRepository<Int, TestVO, TestEntities> {
        override val table = TestEntities
        override fun relationObjectMapping(it: ResultRow): TestVO {
            val vo = TestVO(it[TestEntities.value])
            vo.id = it[TestEntities.id].value
            return vo
        }

        override fun new(vo: TestVO): TestVO {
            TODO("Not yet implemented")
        }

        override fun update(vo: TestVO): TestVO {
            TODO("Not yet implemented")
        }
    }

    private val testRepository = TestRepository()
    private lateinit var testEntity: TestEntity

    @BeforeEach
    fun resetTestState() {
        resetDataBase()
        testEntity = insert((Math.random() * 10).toInt())
    }

    private fun insert(value: Int): TestEntity = transaction {
        TestEntity.new { this.value = value }
    }

    @Test
    fun findById() = transaction {
        val vo = testRepository.findById(testEntity.id.value)
        assertNotNull(vo)
        vo!!
        assertEquals(testEntity.id.value, vo.id)
        assertEquals(testEntity.value, vo.value)
    }

    @Test
    fun find() {
        val voList = testRepository.find()
        assertEquals(1, voList.size)
        assertEquals(testEntity.id.value, voList[0].id)
        assertEquals(testEntity.value, voList[0].value)
        val voList2 = testRepository.find(offset = 2)
        assertEquals(0, voList2.size)
    }

    @Test
    fun update() {
        val updated = testRepository.update {
            this.id eq testEntity.id
            it[id] = DaoEntityID(testEntity.id.value + 1, TestEntities)
        }
        val updatedEntity = transaction { TestEntity.findById(testEntity.id.value + 1) }

        assertEquals(1, updated)
        assertNotNull(updatedEntity)
    }

    @Test
    fun deleteById() {
        testRepository.deleteById(testEntity.id.value)
        val entity = transaction { TestEntity.findById(testEntity.id.value) }
        assertNull(entity)
    }

    @Test
    fun deleteByVO() {
        val vo = TestVO(1)
        vo.id = testEntity.id.value
        testRepository.delete(vo)
        val entity = transaction { TestEntity.findById(testEntity.id.value) }
        assertNull(entity)
    }

    @Test
    fun deleteByExpression() {
        testRepository.delete { this.id eq testEntity.id }
        val entity = transaction { TestEntity.findById(testEntity.id.value) }
        assertNull(entity)
    }
}
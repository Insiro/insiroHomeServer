package me.insiro.home.server.application

import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.domain.entity.IEntityVO
import me.insiro.home.server.application.domain.entity.IntBaseTable
import me.insiro.home.server.testUtils.AbsDataBaseTest
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)

class AbsRepositoryTest : AbsDataBaseTest(TestEntities) {
    object TestEntities : IntBaseTable() {
        val value = integer("value")
    }

    // Exposed DAO Pattern
    class TestEntity(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<TestEntity>(TestEntities)

        var value by TestEntities.value
    }

    data class TestVO(
        var value: Int,
        override val id: Id? = null,
    ) : IEntityVO<Int> {
        @JvmInline
        value class Id(override val value: Int) : IEntityVO.Id<Int> {
            constructor(id: EntityID<Int>) : this(id.value)
        }
    }

    class TestRepository : AbsRepository<Int, TestEntities, TestVO, TestVO.Id> {
        override val table = TestEntities
        override fun relationObjectMapping(it: ResultRow): TestVO {
            val id = TestVO.Id(it[TestEntities.id].value)
            return TestVO(it[TestEntities.value], id = id)
        }

        override fun new(vo: TestVO): TestVO {
            throw Exception("Will Not Tested")
        }

        override fun update(vo: TestVO): TestVO {
            throw Exception("Will Not Tested")
        }
    }

    private val testRepository = TestRepository()
    private lateinit var testEntity: TestEntity
    override fun resetDataBase() = transaction {
        SchemaUtils.drop(TestEntities)
        SchemaUtils.create(TestEntities)
    }

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
        val vo = testRepository.findById(TestVO.Id(testEntity.id))
        assertNotNull(vo)
        vo!!.id
        assertEquals(testEntity.id.value, vo.id!!.value)
        assertEquals(testEntity.value, vo.value)
    }

    @Test
    fun find() {
        val voList = testRepository.find()
        assertEquals(1, voList.size)
        assertEquals(testEntity.id.value, voList[0].id!!.value)
        assertEquals(testEntity.value, voList[0].value)
        val voList2 = testRepository.find(limitOption = OffsetLimit(2, 10))
        assertEquals(0, voList2.size)
    }

    @Test
    fun deleteById() {
        testRepository.delete(TestVO.Id(testEntity.id))
        val entity = transaction { TestEntity.findById(testEntity.id) }
        assertNull(entity)
    }

    @Test
    fun deleteByVO() {
        val vo = TestVO(1, id = TestVO.Id(testEntity.id))
        testRepository.delete(vo)
        val entity = transaction { TestEntity.findById(testEntity.id) }
        assertNull(entity)
    }

    @Test
    fun deleteByExpression() {
        testRepository.delete(TestVO.Id(testEntity.id))
        val entity = transaction { TestEntity.findById(testEntity.id) }
        assertNull(entity)
    }
}
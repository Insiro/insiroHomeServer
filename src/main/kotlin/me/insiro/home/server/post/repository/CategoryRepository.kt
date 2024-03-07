package me.insiro.home.server.post.repository

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.post.entity.Categories
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.exception.CategoryConflictException
import me.insiro.home.server.post.exception.CategoryWrongFieldException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository

@Repository
class CategoryRepository : AbsRepository<Int, Categories, Category, Category.Id> {
    override val table = Categories
    override fun relationObjectMapping(it: ResultRow): Category {
        return Category(
            it[Categories.name],
            Category.Id(it[Categories.id].value)
        )
    }

    override fun update(vo: Category): Category = transaction {
        assert( vo.id !=null)
        findByName(vo.name)?.let { throw CategoryConflictException(vo.name) }
        try {
            Categories.update {
                Categories.id eq vo.id!!.value
                it[name] = vo.name
            }
            vo

        } catch (e: IllegalArgumentException) {
            throw CategoryWrongFieldException(vo)
        }

    }

    override fun new(vo: Category): Category = transaction {
        findByName(vo.name)?.let { throw CategoryConflictException(vo.name) }
        try {
            val id = Categories.insertAndGetId { it[name] = vo.name }
            vo.copy(id = Category.Id(id))
        } catch (e: IllegalArgumentException) {
            throw CategoryWrongFieldException(vo)
        }
    }

    fun findByName(name: String): Category? = transaction {
        val query = Categories.selectAll().where { Categories.name eq name }.firstOrNull()
        query?.let { relationObjectMapping(query) }
    }
}
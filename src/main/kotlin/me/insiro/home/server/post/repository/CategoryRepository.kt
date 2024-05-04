package me.insiro.home.server.post.repository

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.post.entity.Categories
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.exception.category.CategoryConflictException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CategoryRepository : AbsRepository<Int, Categories, Category, Category.Id> {
    override val table = Categories
    override fun relationObjectMapping(it: ResultRow): Category {
        return Category(
            it[Categories.name],
            Category.Id(it[Categories.id].value),
            it[Categories.createdAt]
        )
    }

    override fun update(vo: Category): Category = transaction {
        assert(vo.id != null)
        findByName(vo.name)?.let { throw CategoryConflictException(vo.name) }
        Categories.update({ Categories.id eq vo.id!!.value }) { it[name] = vo.name }
        vo
    }

    override fun new(vo: Category): Category = transaction {
        findByName(vo.name)?.let { throw CategoryConflictException(vo.name) }
        val now = LocalDateTime.now()
        val id = Categories.insertAndGetId {
            it[name] = vo.name
            it[createdAt] = now
        }
        vo.copy(id = Category.Id(id), createdAt = now)
    }

    fun findByName(name: String): Category? = transaction {
        val query = Categories.selectAll().where { Categories.name eq name }.firstOrNull()
        query?.let { relationObjectMapping(query) }
    }
}
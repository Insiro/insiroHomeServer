package me.insiro.home.server.post.repository

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.post.entity.Categories
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.exception.category.CategoryConflictException
import me.insiro.home.server.post.exception.category.CategoryNotFoundException
import org.jetbrains.exposed.sql.ResultRow
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
            Category.Id(it[Categories.id].value),
        )
    }

    override fun update(vo: Category): Category = transaction {
        assert(vo.id != null)
        if (findByName(vo.name).isSuccess)
            throw CategoryConflictException(vo.name)
        Categories.update({ Categories.id eq vo.id!!.value }) { it[name] = vo.name }
        vo
    }

    override fun new(vo: Category): Category = transaction {
        if (findByName(vo.name).isSuccess)
            throw CategoryConflictException(vo.name)
        val id = Categories.insertAndGetId { it[name] = vo.name }
        vo.copy(id = Category.Id(id))
    }

    fun findByName(name: String): Result<Category> = transaction {
        Categories.selectAll().where { Categories.name eq name }.firstOrNull()
            ?.let { Result.success(relationObjectMapping(it)) } ?: Result.failure(CategoryNotFoundException(name))
    }
}
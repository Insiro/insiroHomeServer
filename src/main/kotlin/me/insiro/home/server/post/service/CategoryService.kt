package me.insiro.home.server.post.service

import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.post.dto.category.ModifyCategoryDTO
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.exception.category.CategoryNotFoundException
import me.insiro.home.server.post.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {
    fun findByName(name: String): Result<Category> {
        return categoryRepository.findByName(name)
            ?.let { Result.success(it) }
            ?: Result.failure(CategoryNotFoundException(name))
    }

    fun findById(id: Category.Id): Result<Category> {
        return categoryRepository.findById(id)
            ?.let { Result.success(it) }
            ?: Result.failure(CategoryNotFoundException(id))
    }

    fun delete(name: String): Result<Category.Id> {
        val category = findByName(name).getOrElse { return Result.failure(CategoryNotFoundException(name)) }
        category.let { categoryRepository.delete(category) }
        return Result.success(category.id!!)
    }


    fun create(dto: ModifyCategoryDTO): Category {
        return categoryRepository.new(Category(dto.name))
    }

    fun update(name: String, dto: ModifyCategoryDTO): Result<Category> {
        val category = findByName(name).getOrElse { return Result.failure(it) }
        val updated = categoryRepository.update(category.copy(name = dto.name))
        return Result.success(updated)
    }

    fun update(id: Category.Id, dto: ModifyCategoryDTO): Result<Category> {
        val category = findById(id).getOrElse { return Result.failure(it) }
        val updated = categoryRepository.update(category.copy(name = dto.name))
        return Result.success(updated)
    }

    fun findAll(offsetLimit: OffsetLimit?): List<Category> {
        return categoryRepository.find(offsetLimit)
    }

}
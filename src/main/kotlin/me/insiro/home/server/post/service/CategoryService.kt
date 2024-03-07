package me.insiro.home.server.post.service

import me.insiro.home.server.application.domain.OffsetLimit
import me.insiro.home.server.post.dto.category.ModifyCategoryDTO
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {
    fun findByName(name: String): Category? {
        return categoryRepository.findByName(name)
    }

    fun findById(id: Category.Id): Category? {
        return categoryRepository.findById(id)
    }

    fun delete(name: String): Category.Id? {
        val categoryId = categoryRepository.findByName(name)?.id
        categoryId?.let { categoryRepository.delete(it) }
        return categoryId
    }


    fun create(dto: ModifyCategoryDTO): Category? {
        val category = Category(dto.name)
        return categoryRepository.new(category)
    }

    fun update(name: String, dto: ModifyCategoryDTO): Category? {
        val category = categoryRepository.findByName(name) ?: return null
        val newCate = category.copy(name = dto.name)
        return categoryRepository.update(newCate)
    }

    fun update(id: Category.Id, dto: ModifyCategoryDTO): Category? {
        val category = categoryRepository.findById(id) ?: return null
        val newCate = category.copy(name = dto.name)
        return categoryRepository.update(newCate)
    }

    fun findAll(offsetLimit: OffsetLimit?): List<Category> {
        return categoryRepository.find(offsetLimit)
    }

}
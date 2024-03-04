package me.insiro.home.server.post.service

import me.insiro.home.server.post.dto.category.ModifyCategoryDTO
import me.insiro.home.server.post.entity.Category
import org.springframework.stereotype.Service

@Service
class CategoryService {
    fun findById(name: String): Category? {
        TODO("Not Yet Implemented")
    }

    fun findById(id: Category.Id): Category? {
        TODO("Not Yet Implemented")

    }

    fun delete(name: String): Category.Id {
        TODO("Not Yet Implemented")
    }


    fun create(dto: ModifyCategoryDTO): Category? {
        TODO("Not Yet Implemented")

    }

    fun update(id: Category.Id, dto: ModifyCategoryDTO): Category {
        TODO("Not Yet Implemented")
    }

    fun findAll(): List<Category> {
        TODO("Not Yet Implemented")
    }

}
package me.insiro.home.server.post.dto.category

import me.insiro.home.server.application.domain.IResponseDTO
import me.insiro.home.server.post.entity.Category
import java.time.LocalDateTime

data class CategoryDTO(
    val name: String,
    override val id: Int,
    override val createdAt: LocalDateTime,
) : IResponseDTO<Int> {
    constructor(category: Category) : this(category.name, category.id!!.value, category.createdAt!!)
}

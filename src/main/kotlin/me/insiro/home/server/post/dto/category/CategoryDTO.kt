package me.insiro.home.server.post.dto.category

import me.insiro.home.server.application.domain.dto.IResponseDTO
import me.insiro.home.server.post.entity.Category

data class CategoryDTO(
    val name: String,
    override val id: Int,
) : IResponseDTO<Int> {
    constructor(category: Category) : this(category.name, category.id!!.value)
}

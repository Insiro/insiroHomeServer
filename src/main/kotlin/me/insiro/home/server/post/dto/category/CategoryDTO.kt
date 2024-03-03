package me.insiro.home.server.post.dto.category

import me.insiro.home.server.post.entity.Category

data class CategoryDTO(val name: String, val id:Int){
    constructor(category: Category):this(category.name, category.id!!.value)
}

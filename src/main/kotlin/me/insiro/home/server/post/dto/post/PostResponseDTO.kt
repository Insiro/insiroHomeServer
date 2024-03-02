package me.insiro.home.server.post.dto.post

import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.post.dto.category.CategoryDTO
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.user.dto.UserDTO


data class PostResponseDTO(
    val id: Post.Id,
    val title: String,
    val author: UserDTO,
    val category: CategoryDTO,
    val status: Status,
){
    constructor(post:Post, author:UserDTO, category: CategoryDTO):this(
        post.id!!,
        post.title,
        author,
        category,
        post.status
    )
}
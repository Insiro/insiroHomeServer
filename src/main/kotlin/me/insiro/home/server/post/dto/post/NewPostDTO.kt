package me.insiro.home.server.post.dto.post

import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.post.entity.Post


data class NewPostDTO(
    val title: String,
    val category: String?,
    val content: String,
    val status: Status?,
    val id: Post.Id? = null // Create With Specified ID
)
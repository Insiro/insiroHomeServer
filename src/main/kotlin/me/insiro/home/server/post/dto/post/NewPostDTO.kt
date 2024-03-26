package me.insiro.home.server.post.dto.post

import me.insiro.home.server.application.domain.Status


data class NewPostDTO(
    val title: String,
    val category: String?,
    val content: String,
    val status: Status = Status.PUBLISHED,
)
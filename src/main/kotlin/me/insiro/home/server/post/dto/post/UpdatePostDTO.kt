package me.insiro.home.server.post.dto.post

import me.insiro.home.server.application.domain.Status


data class UpdatePostDTO(
    val title: String? = null,
    val category: String? = null,
    val status: Status? = null,
    val content: String? = null,
)
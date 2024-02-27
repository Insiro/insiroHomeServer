package me.insiro.home.server.post.dto.post

import me.insiro.home.server.application.domain.Status


data class PostResponseDTO(
        val id: Long,
        val title: String,
        val authorId: Long,
        val authorName:String,
        val category: String?,
        val status: Status,
        val fileName: String,
)
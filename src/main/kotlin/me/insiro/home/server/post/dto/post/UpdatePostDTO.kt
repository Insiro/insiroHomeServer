package me.insiro.home.server.post.dto.post

import me.insiro.home.server.application.domain.Status


data class UpdatePostDTO (
        val title: String?,
        val category:String?,
        val status: Status?,
        val content:String?
)
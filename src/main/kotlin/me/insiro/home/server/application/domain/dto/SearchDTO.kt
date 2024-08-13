package me.insiro.home.server.application.domain.dto

import me.insiro.home.server.post.dto.post.PostResponseDTO
import me.insiro.home.server.project.dto.project.ProjectDTO

data class SearchDTO(
    val projects: List< ProjectDTO>,
    val posts:List<PostResponseDTO>
)
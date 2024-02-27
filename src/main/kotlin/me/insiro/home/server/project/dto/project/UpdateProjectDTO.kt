package me.insiro.home.server.project.dto.project

import me.insiro.home.server.application.domain.Status


data class UpdateProjectDTO(
        val title: String?,
        val status: Status?,
        val content: String?,
        val languages: List<String>?,
        val type: List<String>?,
)

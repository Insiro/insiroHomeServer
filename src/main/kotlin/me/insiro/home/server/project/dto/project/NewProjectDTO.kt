package me.insiro.home.server.project.dto.project

import me.insiro.home.server.application.domain.Status

data class NewProjectDTO(
        val title: String,
        val status: Status?,
        val content: String,
        val types: List<String>
)

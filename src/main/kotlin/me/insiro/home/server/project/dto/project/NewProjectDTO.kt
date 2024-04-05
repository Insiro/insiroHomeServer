package me.insiro.home.server.project.dto.project

import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.project.entity.Project

data class NewProjectDTO(
    val title: String,
    val status: Status?,
    val content: String,
    val types: List<String>?,
    val id: Project.Id? = null// Create With Specified ID
)

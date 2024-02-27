package me.insiro.home.server.project.dto.project

import jdk.jshell.Snippet.Status

data class ProjectDTO(
        val id: Long,
        val status: Status,
        val filename: String,
        val languages: List<String>,
        val type: List<String>,
)

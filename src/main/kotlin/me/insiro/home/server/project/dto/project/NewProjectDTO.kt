package me.insiro.home.server.project.dto.project

import jdk.jshell.Snippet.Status

data class NewProjectDTO(
        val title: String,
        val status: Status?,
        val content: String,
        val languages: List<String>,
        val type: List<String>
)

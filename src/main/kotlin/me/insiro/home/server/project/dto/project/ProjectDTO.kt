package me.insiro.home.server.project.dto.project

import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.entity.ProjectType
import java.time.LocalDateTime
import java.util.*

data class ProjectDTO(
    val id: UUID,
    val title: String,
    val status: Status,
    var type: List<ProjectType>? = null,
    val createdAt: LocalDateTime?,
    val content: String?,
) {
    constructor(project: Project, content: String? = null) : this(
        project.id!!.value, project.title, project.status,
        when (project) {
            is Project.Joined -> project.types
            is Project.Raw -> null
        },
        project.createdAt,
        content,
    )
}

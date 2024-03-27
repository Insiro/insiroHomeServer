package me.insiro.home.server.project.dto.project

import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.entity.ProjectType
import java.util.*

data class ProjectDTO(
    val id: UUID,
    val status: Status,
    var type: List<ProjectType>? = null,
) {
    constructor(project: Project) : this(
        project.id!!.value, project.status,
        when (project) {
            is Project.Joined -> project.types
            is Project.Raw -> null
        }
    )
}

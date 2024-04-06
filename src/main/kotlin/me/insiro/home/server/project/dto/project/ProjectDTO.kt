package me.insiro.home.server.project.dto.project

import me.insiro.home.server.application.domain.dto.IFileIcon
import me.insiro.home.server.application.domain.dto.IResponseDTO
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.entity.ProjectType
import java.time.LocalDateTime
import java.util.*

data class ProjectDTO(
    override val id: UUID,
    val title: String,
    val status: Status,
    var type: List<ProjectType>? = null,
    override val createdAt: LocalDateTime?,
    val content: String?,
    override var icon: Boolean = false
) : IFileIcon, IResponseDTO<UUID> {
    constructor(project: Project, content: String? = null, icon: Boolean = false) : this(
        project.id!!.value, project.title, project.status,
        when (project) {
            is Project.Joined -> project.types
            is Project.Raw -> null
        },
        project.createdAt,
        content,
        icon
    )
}

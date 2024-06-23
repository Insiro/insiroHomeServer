package me.insiro.home.server.project.dto.project

import me.insiro.home.server.application.domain.dto.IFileIcon
import me.insiro.home.server.application.domain.dto.IResponseDTO
import me.insiro.home.server.application.domain.entity.ICreatedAt
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.entity.ProjectType
import java.time.LocalDateTime

data class ProjectDTO(
    override val id: Long,
    val title: String,
    val status: Status,
    var type: List<ProjectType>? = null,
    override val createdAt: LocalDateTime?,
    val content: String?,
    override var icon: String? = null
) : IFileIcon,IResponseDTO<Long>, ICreatedAt {
    constructor(project: Project, content: String? = null, icon: String? = null) : this(
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

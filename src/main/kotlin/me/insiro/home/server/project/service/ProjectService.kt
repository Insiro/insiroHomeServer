package me.insiro.home.server.project.service

import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.project.dto.project.NewProjectDTO
import me.insiro.home.server.project.dto.project.UpdateProjectDTO
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.entity.ProjectType
import me.insiro.home.server.project.repository.ProjectRepository
import me.insiro.home.server.project.repository.ProjectTypeRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class ProjectService(val projectRepository: ProjectRepository, val typeRepository: ProjectTypeRepository) {

    private fun updateRelation(typeNames: List<String>, projectId: Project.Id): List<ProjectType> = transaction {
        typeRepository.delete(projectId)
        typeNames.map { typeRepository.addRelationOrInsert(projectId, it) }
    }

    fun create(dto: NewProjectDTO): Result<Project> {
        val project = projectRepository.new(Project.Raw(dto.title, dto.status ?: Status.PUBLISHED))
        return Result.success( Project.Joined(project, dto.types?.let { types -> updateRelation(types, project.id!!) }))
    }

    fun update(title: String, dto: UpdateProjectDTO): Result<Project.Joined> = runCatching {
        val project =
            if (dto.title == null && dto.status == null)
                projectRepository.findByTitle(title).getOrThrow()
            else projectRepository.update(title, dto.title, dto.status).getOrThrow()
        val types = dto.types?.let { updateRelation(it, project.id!!) } ?: typeRepository.find(project.id)
        Project.Joined(project, types)
    }

    fun find(filterOption: List<Status>? = null, offsetLimit: OffsetLimit? = null): List<Project> {
        return projectRepository.find(filterOption, offsetLimit)
    }

    fun findJoined(filterOption: List<Status>? = null, offsetLimit: OffsetLimit? = null): List<Project.Joined> {
        return projectRepository.find(filterOption, offsetLimit)
            .map { Project.Joined(it, typeRepository.find(projectId = it.id)) }
    }

    fun get(title: String): Result<Project.Joined> = kotlin.runCatching {
        val project = projectRepository.findByTitle(title).getOrThrow()
        val types = typeRepository.find(projectId = project.id)
        Project.Joined(project, types)
    }

    fun delete(project: Project): Result<Project> {
        projectRepository.delete(project)
        return Result.success(project)
    }
}
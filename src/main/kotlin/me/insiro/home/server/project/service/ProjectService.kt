package me.insiro.home.server.project.service

import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.project.dto.project.NewProjectDTO
import me.insiro.home.server.project.dto.project.UpdateProjectDTO
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.entity.ProjectType
import me.insiro.home.server.project.exception.ProjectDuplicatedException
import me.insiro.home.server.project.exception.ProjectNotFoundException
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
        val project = when (val id = dto.id) {
            null -> projectRepository.new(Project.Raw(dto.title, dto.status ?: Status.PUBLISHED))
            else -> projectRepository.new(Project.Raw(dto.title, dto.status ?: Status.PUBLISHED), id)
        } ?: return Result.failure(ProjectDuplicatedException(dto.id!!))
        return Result.success(Project.Joined(project, dto.types?.let { types -> updateRelation(types, project.id!!) }))
    }

    fun update(id: Project.Id, dto: UpdateProjectDTO): Project.Joined {
        val project = projectRepository.update(id, dto.title, dto.status)
        val types = dto.types?.let { updateRelation(it, project.id!!) } ?: typeRepository.find(id)
        return Project.Joined(project, types)
    }

    fun find(filterOption: List<Status>? = null, offsetLimit: OffsetLimit? = null): List<Project> {
        return projectRepository.find(filterOption, offsetLimit)
    }

    fun findJoined(filterOption: List<Status>? = null, offsetLimit: OffsetLimit? = null): List<Project.Joined> {
        return projectRepository.find(filterOption, offsetLimit)
            .map { Project.Joined(it, typeRepository.find(projectId = it.id)) }
    }

    fun get(id: Project.Id): Result<Project.Joined> {
        val project = projectRepository.findById(id) ?: return Result.failure(ProjectNotFoundException(id))
        val types = typeRepository.find(projectId = id)
        return Result.success(Project.Joined(project, types))
    }

    fun delete(project: Project): Result<Project> {
        projectRepository.delete(project)
        return Result.success(project)
    }
}
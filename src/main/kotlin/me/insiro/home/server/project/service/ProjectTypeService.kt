package me.insiro.home.server.project.service

import me.insiro.home.server.project.dto.type.ModifyProjectTypeDTO
import me.insiro.home.server.project.entity.ProjectType
import me.insiro.home.server.project.exception.ProjectTypeNotFoundException
import me.insiro.home.server.project.repository.ProjectTypeRepository
import org.springframework.stereotype.Service

@Service
class ProjectTypeService(private val typeRepository: ProjectTypeRepository) {
    fun find(): List<ProjectType> {
        return typeRepository.find(limitOption = null)
    }

    fun get(id: ProjectType.Id): Result<ProjectType> {
        return typeRepository.findById(id)
            ?.let { Result.success(it) }
            ?: Result.failure(ProjectTypeNotFoundException(id))
    }

    fun create(dto: ModifyProjectTypeDTO): Result<ProjectType> {
        return Result.success(typeRepository.new(ProjectType(dto.name.uppercase())))
    }

    fun update(id: ProjectType.Id, dto: ModifyProjectTypeDTO): Result<ProjectType> {
        val type = typeRepository.findById(id) ?: return Result.failure(ProjectTypeNotFoundException(id))
        return Result.success(typeRepository.update(type.copy(name = dto.name.uppercase())))
    }

    fun delete(id: ProjectType.Id): Boolean {
        return typeRepository.delete(id)
    }

}
package me.insiro.home.server.project.controller

import me.insiro.home.server.project.dto.type.ModifyProjectTypeDTO
import me.insiro.home.server.project.dto.type.ProjectTypeDTO
import me.insiro.home.server.project.entity.ProjectType
import me.insiro.home.server.project.service.ProjectTypeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("projects/types")
class ProjectTypeController(private val service: ProjectTypeService) {
    @GetMapping
    fun getTypes(): ResponseEntity<List<ProjectTypeDTO>> {
        return ResponseEntity(service.find().map(::ProjectTypeDTO), HttpStatus.OK)
    }

    @PostMapping
    fun createType(@RequestBody dto: ModifyProjectTypeDTO): ResponseEntity<ProjectTypeDTO> {
        return ResponseEntity(service.create(dto).getOrThrow().let(::ProjectTypeDTO), HttpStatus.CREATED)
    }

    @GetMapping("{id}")
    fun getById(@PathVariable id: ProjectType.Id): ResponseEntity<ProjectTypeDTO> {
        return ResponseEntity(service.get(id).getOrThrow().let(::ProjectTypeDTO), HttpStatus.OK)
    }

    @PatchMapping("{id}")
    fun addType(
        @PathVariable id: ProjectType.Id,
        @RequestBody dto: ModifyProjectTypeDTO
    ): ResponseEntity<ProjectTypeDTO> {
        return ResponseEntity(service.update(id, dto).getOrThrow().let(::ProjectTypeDTO), HttpStatus.OK)
    }

    @DeleteMapping("{id}")
    fun deleteType(@PathVariable id: ProjectType.Id): ResponseEntity<String> {
        service.delete(id)
        return ResponseEntity("success", HttpStatus.OK)
    }


}
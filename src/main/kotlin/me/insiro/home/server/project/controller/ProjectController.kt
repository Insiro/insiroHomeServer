package me.insiro.home.server.project.controller

import me.insiro.home.server.application.ISignedController
import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.file.service.ProjectFileService
import me.insiro.home.server.project.dto.project.NewProjectDTO
import me.insiro.home.server.project.dto.project.ProjectDTO
import me.insiro.home.server.project.dto.project.UpdateProjectDTO
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.service.ProjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("projects")
class ProjectController(
    private val projectService: ProjectService,
    private val fileService: ProjectFileService,
) :
    ISignedController {
    @GetMapping
    fun getProjects(
        @RequestParam(required = false) offset: Long = 0,
        @RequestParam(required = false) limit: Int? = null,
        @RequestParam(required = false) status: List<Status> = listOf(Status.PUBLISHED),
    ): ResponseEntity<List<ProjectDTO>> {
        val offsetLimit = limit?.let { OffsetLimit(offset, limit) }
        val projects = projectService.findJoined(filterOption = status, offsetLimit = offsetLimit)
            .map { ProjectDTO(it, icon = fileService.existIcon(it)) }
        return ResponseEntity(projects, HttpStatus.OK)
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    fun newProject(
        @RequestPart("data") newProjectDTO: NewProjectDTO,
        @RequestParam("files") files: List<MultipartFile>? = null
    ): ResponseEntity<ProjectDTO> {
        val project = projectService.create(newProjectDTO).getOrThrow()
        fileService.create(project, newProjectDTO.content, files)
        val existIcon = fileService.existIcon(project)
        return ResponseEntity(ProjectDTO(project, content = newProjectDTO.content, existIcon), HttpStatus.CREATED)
    }

    @GetMapping("{id}")
    fun getProjectById(@PathVariable id: Project.Id): ResponseEntity<ProjectDTO> {
        val project = projectService.get(id).getOrThrow()
        val loaded = fileService.get(vo = project, load = true)
        val icon = fileService.existIcon(project)
        return ResponseEntity(ProjectDTO(project, loaded?.content, icon), HttpStatus.OK)
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping("{id}")
    fun updateProject(
        @PathVariable id: Project.Id,
        @RequestPart("data") updateDTO: UpdateProjectDTO,
        @RequestParam("files") files: List<MultipartFile>?
    ): ResponseEntity<ProjectDTO> {
        val project = projectService.update(id, updateDTO)
        fileService.update(project, updateDTO, files)
        val icon = fileService.existIcon(project)
        return ResponseEntity(ProjectDTO(project,  icon = icon), HttpStatus.OK)
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("{id}")
    fun deleteProject(@PathVariable id: Project.Id) {
        val project = projectService.get(id).getOrThrow()
        projectService.delete(project)
        fileService.delete(project)
        return
    }
}
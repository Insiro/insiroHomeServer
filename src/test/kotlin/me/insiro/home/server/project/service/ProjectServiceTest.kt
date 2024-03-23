package me.insiro.home.server.project.service

import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.project.dto.project.NewProjectDTO
import me.insiro.home.server.project.dto.project.UpdateProjectDTO
import me.insiro.home.server.project.entity.*
import me.insiro.home.server.project.exception.ProjectNotFoundException
import me.insiro.home.server.project.repository.ProjectRepository
import me.insiro.home.server.project.repository.ProjectTypeRepository
import me.insiro.home.server.testUtils.AbsDataBaseTest
import me.insiro.home.server.testUtils.DBInserter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectServiceTest : AbsDataBaseTest(Projects, ProjectTypes, ProjectTypeRelations) {
    private val typeRepository = ProjectTypeRepository()
    private val projectRepository = ProjectRepository()
    private val projectService = ProjectService(projectRepository, typeRepository)
    private lateinit var project: Project
    private lateinit var type: ProjectType

    @BeforeEach
    fun initTest() {
        resetDataBase()
        project = DBInserter.insertProject(Project.Raw("title"))
        type = DBInserter.insertProjectType(ProjectType("type"))
        DBInserter.insertProjectTypeRelation(project, type)
    }

    @Test
    fun getProject() {
        val found = projectService.find()
        assertEquals(1, found.size)
        assertEquals(found[0].title, project.title)
    }

    @Test
    fun `create new Project and Get Id`() {
        val dto = NewProjectDTO("title", Status.PUBLISHED, "content", arrayListOf("newType", "type"))
        val id = projectService.create(dto).id!!
        val result = projectService.get(id).getOrNull()
        assertNotNull(result)
        assertEquals(dto.title, result!!.title)
        assertEquals(dto.status, result.status)
        assertEquals(dto.types?.sorted(), result.types?.map{it.name}?.sorted())
        assertNotNull(result.createdAt)
        assertNotNull(result.id)
    }

    @Test
    fun updateProject() {
        val updateDTO = UpdateProjectDTO("newTitle", null, null, null, null)
        val updated = projectService.update(project.id!!, updateDTO)
        val found = projectService.get(project.id!!).getOrThrow()
        assertEquals(updated.types, found.types)
        assertEquals(updateDTO.title, updated.title)
    }

    @Test
    fun deleteProject() {
        projectService.delete(project)
        org.junit.jupiter.api.assertThrows<ProjectNotFoundException> { projectService.get(project.id!!).getOrThrow() }
    }
}
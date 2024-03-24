package me.insiro.home.server.project

import me.insiro.home.server.file.service.ProjectFileService
import me.insiro.home.server.project.dto.project.NewProjectDTO
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.service.ProjectService
import me.insiro.home.server.testUtils.AbsControllerTest
import me.insiro.home.server.user.dto.AuthDetail
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.mock.web.MockPart
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ProjectControllerTest : AbsControllerTest("projects") {
    private val projectService = mock(ProjectService::class.java)
    private val fileService = mock(ProjectFileService::class.java)
    private lateinit var controller: ProjectController
    private lateinit var project: Project.Joined
    private val detail =
        AuthDetail(User("testUser", "", "testEmail", UserRole.ROLE_ADMIN.key, id = User.Id(1), LocalDateTime.now()))

    @BeforeEach
    override fun init() {
        controller = ProjectController(projectService, fileService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        project = Project.Joined("title", id = Project.Id(1), createdAt = LocalDateTime.now())
    }

    @Test
    fun getProjects() {
        Mockito.`when`(projectService.find()).thenReturn(arrayListOf())
        mockMvc.perform(MockMvcRequestBuilders.get(uri))
            .andExpect { status().isOk }
            .andExpect { ResultMatcher { jsonPath("$").isArray } }
    }

    @Test
    fun getProjectById() {
        Mockito.`when`(projectService.get(project.id!!)).thenReturn(Result.success(project))
        mockMvc.perform(MockMvcRequestBuilders.get(uri(project.id!!)))
            .andExpect { status().isOk }
            .andExpect { ResultMatcher { jsonPath("$.id").value(project.id) } }
            .andExpect { ResultMatcher { jsonPath("$.title").value(project.title) } }
            .andExpect { ResultMatcher { jsonPath("$.status").value(project.status) } }
            .andExpect { ResultMatcher { jsonPath("$.types").isArray } }
    }

    @Test
    fun newProject() {
        val newDTO = NewProjectDTO(project.title, project.status, "content", project.types?.map { it.name })
        val dtoPart = MockPart("data", gson.toJson(newDTO).toByteArray())
        dtoPart.headers.contentType = MediaType.APPLICATION_JSON
        Mockito.`when`(projectService.create(newDTO)).thenReturn(project)
        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart(uri)
                .part(dtoPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .apply {
                    SecurityContextHolder.getContext().authentication =
                        UsernamePasswordAuthenticationToken(detail, "", detail.authorities)
                }
        )
            .andExpect { status().isCreated }
            .andExpect { jsonPath("$.status").value(newDTO.status) }
    }


    @Test
    fun deleteProject() {
        Mockito.`when`(projectService.delete(project)).thenReturn(Result.success(project))
        mockMvc.perform(MockMvcRequestBuilders.delete(uri(project.id!!)))
            .andExpect { status().isOk }
    }


}
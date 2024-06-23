package me.insiro.home.server.project.controller

import me.insiro.home.server.project.dto.type.ModifyProjectTypeDTO
import me.insiro.home.server.project.entity.ProjectType
import me.insiro.home.server.project.service.ProjectTypeService
import me.insiro.home.server.testUtils.AbsControllerTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class ProjectTypeControllerTest : AbsControllerTest("projects/types") {
    private val type = ProjectType("TEST_TYPE", id = ProjectType.Id(1))
    private val service = mock(ProjectTypeService::class.java)
    private lateinit var controller: ProjectTypeController

    @BeforeEach
    override fun init() {
        controller = ProjectTypeController(service)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `test get`() {
        Mockito.`when`(service.find()).thenReturn(arrayListOf())
        mockMvc.perform(MockMvcRequestBuilders.get(uri))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isArray }
    }

    @Test
    fun `test create Type`() {
        val dto = ModifyProjectTypeDTO(type.name, false)
        Mockito.`when`(service.create(dto)).thenReturn(Result.success(type))
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(dto))
        ).andExpect { status().isCreated }
            .andExpect { jsonPath("$.name").value(type.name) }
            .andExpect { jsonPath("$.id").value(type.id) }
    }

    @Test
    fun `test getById`() {
        Mockito.`when`(service.get(type.id!!)).thenReturn(Result.success(type))
        mockMvc.perform(MockMvcRequestBuilders.get(uri(type.id!!)))
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.name").value(type.name) }
            .andExpect { jsonPath("$.id").value(type.id) }
    }

    @Test
    fun `test update`() {
        val dto = ModifyProjectTypeDTO("NEW_NAME", false)
        Mockito.`when`(service.update(type.id!!, dto)).thenReturn(Result.success(type.copy(name = dto.name)))
        mockMvc.perform(
            MockMvcRequestBuilders.patch(uri(type.id!!))
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(dto))
        ).andExpect { status().isCreated }
            .andExpect { jsonPath("$.name").value(dto.name) }
            .andExpect { jsonPath("$.id").value(type.id) }
    }

    @Test
    fun `delete test`() {
        Mockito.`when`(service.delete(type.id!!)).thenReturn(true)

        mockMvc.perform(MockMvcRequestBuilders.delete(uri(type.id!!)))
            .andExpect { status().isOk }
    }
}
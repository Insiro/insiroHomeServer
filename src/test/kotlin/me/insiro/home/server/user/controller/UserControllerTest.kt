package me.insiro.home.server.user.controller

import me.insiro.home.server.AbsControllerTest
import me.insiro.home.server.user.UserController
import me.insiro.home.server.user.UserService
import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.dto.UserDTO
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class UserControllerTest : AbsControllerTest("/users") {
    private val mockUserService: UserService = mock(UserService::class.java)
    private lateinit var user: User


    @BeforeEach
    override fun init() {
        val userController = UserController(mockUserService)
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build()
        user = User("testName", "testPwd", "test@example.com", 0b1)
        user.id = 1
    }

    @Test
    fun getUser() {
        val userDTO = UserDTO.fromUser(user)
        Mockito.`when`(mockUserService.getUser(user.id!!)).thenReturn(user)
        mockMvc.perform(MockMvcRequestBuilders.get(uri(user.id)))
                .andExpect { status().isOk }
                .andExpect { jsonPath("$.id").value(userDTO.id) }
                .andExpect { jsonPath("$.name").value(userDTO.name) }
                .andExpect { jsonPath("$.email").value(userDTO.email) }
                .andExpect { jsonPath("$.role").value(userDTO.role) }

    }

    @Test
    fun updateUser() {
        val newName = "testUser2"
        val updateUserDTO = UpdateUserDTO(newName, null, null)
        Mockito.`when`(mockUserService.updateUser(user.id!!, updateUserDTO)).then { user.name = newName; user }.thenReturn(user)
        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(uri(user.id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(updateUserDTO)))
                .andExpect { status().isOk }
                .andExpect { jsonPath("$.id").value(user.id) }
                .andExpect { jsonPath("$.name").value(updateUserDTO.name) }
                .andExpect { jsonPath("$.email").value(updateUserDTO.email) }
    }

    @Test
    fun deleteUser() {
        Mockito.`when`(mockUserService.deleteUser(user.id)).thenReturn(true)
        mockMvc.perform(MockMvcRequestBuilders.delete(uri(user.id)))
                .andExpect { status() }
    }

    @Test
    fun createUser() {
        val newUserDTO = NewUserDTO(user.name, user.password, user.email)
        Mockito.`when`(mockUserService.createUser(newUserDTO)).thenReturn(user)
        mockMvc.perform(MockMvcRequestBuilders
                .post(uri())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newUserDTO)))
                .andExpect { status().isOk }
                .andExpect { jsonPath("$.name").value(newUserDTO.name) }
                .andExpect { jsonPath("$.email").value(newUserDTO.email) }
                .andExpect { jsonPath("$.role").value(arrayOf(UserRole.ROLE_READ_ONLY)) }
    }
}




package me.insiro.home.server.user.controller

import me.insiro.home.server.AbsControllerTest
import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.dto.UserDTO
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.*

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

@RestController
@RequestMapping("users")
class UserController(private val userService: UserService) {
    @GetMapping
    fun getAllUser(@RequestParam(required = false) offset: Long = 0, @RequestParam(required = false) limit: Int? = null): ResponseEntity<List<UserDTO>> {
        val users = userService.getUsers(offset, limit).map(UserDTO.Companion::fromUser)
        return ResponseEntity(users, HttpStatus.OK)
    }

    @PostMapping
    fun createUser(@RequestBody newUserDTO: NewUserDTO): ResponseEntity<UserDTO> {
        val user = userService.createUser(newUserDTO)
        return ResponseEntity(UserDTO.fromUser(user), HttpStatus.CREATED)
    }

    @GetMapping("{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<UserDTO> {
        val user = userService.getUser(id) ?: throw UserNotFoundException(id)
        return ResponseEntity(UserDTO.fromUser(user), HttpStatus.OK)
    }

    @PatchMapping("{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody updateUserDTO: UpdateUserDTO): ResponseEntity<UserDTO> {
        val user = userService.updateUser(id, updateUserDTO)
        return ResponseEntity(UserDTO.fromUser(user), HttpStatus.OK)
    }

    @DeleteMapping("{id}")
    fun deleteUser(@PathVariable id: Long): String {
        userService.deleteUser(id)
        return "success"
    }
}

abstract class AbsException(val status: HttpStatus, message: String) : Exception(message)
class UserNotFoundException : AbsException {
    constructor(id: Long) : super(HttpStatus.NOT_FOUND, String.format("User Not Found ( id : %d )", id))
    constructor(name: String) : super(HttpStatus.NOT_FOUND, String.format("User Not Found ( name : %s )", name))
}

class UserService {
    fun getUser(id: Long): User? {
        TODO("Not yet implemented")
    }

    fun updateUser(id: Long, updateUserDTO: UpdateUserDTO): User {
        TODO("Not yet implemented")
    }

    fun deleteUser(id: Long?): Boolean {
        TODO("Not yet implemented")
    }

    fun createUser(newUserDTO: NewUserDTO): User {
        TODO("Not yet implemented")
    }

    fun getUsers(offset: Long, limit: Int?): List<User> {
        TODO("Not yet implemented")
    }
}

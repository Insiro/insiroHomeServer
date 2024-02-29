package me.insiro.home.server.user.controller

import me.insiro.home.server.testUtils.AbsControllerTest
import me.insiro.home.server.user.UserService
import me.insiro.home.server.user.dto.SignInDTO
import me.insiro.home.server.user.dto.UserDTO
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.utils.AuthenticateProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.*

@ExtendWith(MockitoExtension::class)
class AuthControllerTest : AbsControllerTest("/auth") {

    @Mock
    private val userService = mock(UserService::class.java)

    companion object {
        private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
        private const val USER_PWD = "testPwd"
        private const val USER_NAME = "testUser"
        private val user = User("testUser", passwordEncoder.encode(USER_PWD), "email", -1)
    }

    @BeforeEach
    override fun init() {
        val authenticateProvider = AuthenticateProvider(userService, passwordEncoder)
        val authController = AuthController(authenticateProvider)
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build()

    }

    @Test
    fun testSignInAndGetUserInfo() {
        val signDto = SignInDTO(USER_NAME, USER_PWD)
        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth")
                        .content(gson.toJson(signDto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect { status().isOk() }
                .andExpect { jsonPath("$.name").value(USER_NAME) }
                .andExpect { jsonPath("$.email").value(user.email) }

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andExpect { status().isOk }
                .andExpect { jsonPath("$.name").value(USER_NAME) }
                .andExpect { jsonPath("$.email").value(user.email) }
    }

    @Test
    @WithMockUser(username = USER_NAME, password = USER_PWD, roles = ["ROLE_ADMIN"])
    fun testGetUserInfoWithAuthenticatedUser() {
        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andExpect { status().isOk }
                .andExpect { jsonPath("$.name").value(user.name) }
                .andExpect { jsonPath("$.email").value(user.email) }
    }
}
//@RestController

@RestController
@RequestMapping("auth")
class AuthController(private val authenticateProvider: AuthenticateProvider) {
    @GetMapping
    fun getSignedUser(): ResponseEntity<UserDTO> {
        TODO("Not yet implemented")
    }

    @PostMapping
    fun singIn(): ResponseEntity<UserDTO> {
        TODO("Not yet implemented")
    }

    @DeleteMapping
    fun singOut() {
        TODO("Not yet implemented")
    }
}


class AuthDetail(val user: User) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        TODO("Not yet implemented")
    }


    override fun getPassword(): String {
        return user.hashedPassword
    }

    override fun getUsername(): String {
        return user.name
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return false
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}
package me.insiro.home.server.user.controller

import jakarta.servlet.http.HttpServletRequest
import me.insiro.home.server.testUtils.AbsControllerTest
import me.insiro.home.server.user.UserService
import me.insiro.home.server.user.dto.SignInDTO
import me.insiro.home.server.user.dto.UserDTO
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.utils.AuthenticateProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
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

    init {
        user.id = 1L
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
        Mockito.`when`(userService.loadUserByUsername(USER_NAME)).thenReturn(AuthDetail(user))
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
    fun getSignedUser(): ResponseEntity<*> {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal
            if (principal is AuthDetail) {
                val user = UserDTO.fromUser(principal.user)
                return ResponseEntity(user, HttpStatus.OK)
            }
        }
        return ResponseEntity("Not Authenticated", HttpStatus.UNAUTHORIZED)
    }

    @PostMapping
    fun singIn(@RequestBody signInDTO: SignInDTO, request: HttpServletRequest): ResponseEntity<UserDTO> {
        val authentication = authenticateProvider.authenticate(
                UsernamePasswordAuthenticationToken(
                        signInDTO.name,
                        signInDTO.password
                )
        )
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        SecurityContextHolder.setContext(context)
        val detail = authentication.principal as AuthDetail
        return ResponseEntity(UserDTO.fromUser(detail.user), HttpStatus.OK)
    }

    @DeleteMapping
    fun singOut() {
        TODO("Not yet implemented")
    }
}


class AuthDetail(val user: User) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val roles = UserRole.fromPermissionKey(user.permission)
        val authorities = roles.map { GrantedAuthority(it::name) }.toMutableList()
        return authorities
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
package me.insiro.home.server.user

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    private val mockUserRepository = mock(UserRepository::class.java)
    private val userService = UserService(mockUserRepository)
    private lateinit var user: User

    @BeforeEach
    fun init() {
        user = User("testName", "testPwd", "test@example.com", 0b1)
        user.id = 1
    }

    @Test
    fun getUser() {
        val uid = user.id!!
        Mockito.`when`(mockUserRepository.findById(uid)).thenReturn(user)
        Mockito.`when`(mockUserRepository.findById(Mockito.anyLong())).thenReturn(user)

        assertNull(userService.getUser(uid + 5))

        val gUser = userService.getUser(uid)
        assertNotNull(gUser)
        assertEquals(gUser!!.id!!, uid)
        assertEquals(gUser.name, user.name)
        assertEquals(gUser.email, user.email)
        assertEquals(gUser.password, user.password)
        assertEquals(gUser.permission, user.permission)
    }

    @Test
    fun updateUser() {
        val uid = user.id!!
        user.name = "UpdateUser"
        val updateDTO = UpdateUserDTO(user.name, null, null)

        Mockito.`when`(mockUserRepository.update(user)).thenReturn(user)
        val updated = userService.updateUser(uid, updateDTO)
        assertEquals(updateDTO.name, updated.name)
        assertEquals(user.id, updated.id)
        assertEquals(user.email, updated.email)
        assertEquals(user.password, updated.password)
        assertEquals(user.permission, updated.permission)
    }

    @Test
    fun deleteUser() {
        Mockito.`when`(mockUserRepository.delete(uid)).thenReturn(true)
        assertEquals(userService.deleteUser(uid), true)
    }

    @Test
    fun createUser() {
        val passwordEncoder = PasswordEncoder()
        val newUserDTO = NewUserDTO(user.name, "testPassword", user.email)
        user.password = passwordEncoder.encode(newUserDTO.password)

        Mockito.`when`(mockUserRepository.create(user)).thenReturn(user)


        val created = userService.createUser(newUserDTO)
        assertEquals(created.name, newUserDTO.name)
        assertEquals(created.email, newUserDTO.email)
        assertEquals(created.permission, UserRole.ROLE_READ_ONLY.key)
        assertTrue(created.password == passwordEncoder.encode(newUserDTO.password))

    }

    @Test
    fun getUsers() {
        Mockito.`when`(mockUserRepository.find { Users.name eq "" }).thenReturn(arrayListOf(user))
        val ret = userService.getUsers()
        assertTrue(ret.size == 1)
    }
}

class UserRepository : AbsRepository<Long, User, Users>(Users) {


    fun find(ex: SqlExpressionBuilder): List<User> {
        TODO("Not yet implemented")
    }

    override fun new(vo: User): User {
        TODO("Not yet implemented")
    }


    override fun relationObjectMapping(it: ResultRow): User {
        TODO("Not yet implemented")
    }

    override fun update(vo: User): User {
        TODO("Not yet implemented")
    }

    fun update(where: SqlExpressionBuilder.() -> Op<Boolean>, body: Users.(UpdateStatement) -> Unit) {
        TODO("Not yet implemented")
    }

}

class PasswordEncoder : BCryptPasswordEncoder()
class UserService(private val userRepository: UserRepository) {
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

    fun getUsers(offset: Long = 0, limit: Int? = null): List<User> {
        TODO("Not yet implemented")
    }
}
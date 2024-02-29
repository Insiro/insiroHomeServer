package me.insiro.home.server.user

import me.insiro.home.server.testUtils.AbsDataBaseTest
import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import me.insiro.home.server.user.exception.UserConflictExcept
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.annotation.Description
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UserServiceTest : AbsDataBaseTest(arrayListOf(Users)) {
    private val userRepository = UserRepository()
    private val passwordEncoder:PasswordEncoder = BCryptPasswordEncoder()
    private val userService = UserService(userRepository, passwordEncoder)

    private lateinit var user: User

    @BeforeEach
    fun init() {
        resetDataBase()
        user = User("testName", "testPwd", "test@example.com", 0b1)
        user.id = insertUser(user)
    }

    private fun insertUser(user: User): Long = transaction {
        Users.insertAndGetId {
            it[name] = user.name
            it[password] = user.hashedPassword
            it[email] = user.email
            it[permission] = user.permission
            it[Users.createdAt] = user.createdAt
        }.value
    }

    @Test
    @Description("Test Get User With DataBase")
    fun getUser() {
        val uid = user.id!!
        //Return Null when Wrong ID is requested
        assertNull(userService.getUser(uid + 5))

        //Return User's VO when
        val gUser = userService.getUser(uid)
        assertNotNull(gUser)
        gUser!!
        assertEquals(uid, gUser.id)
        assertEquals(user.name, gUser.name)
        assertEquals(user.email, gUser.email)
        assertEquals(user.hashedPassword, gUser.hashedPassword)
        assertEquals(user.permission, gUser.permission)
    }

    @Test
    @Description("Test Update User With DataBase")
    fun updateUser() {
        val uid = user.id!!
        val updateDTO = UpdateUserDTO("UpdateUser", "newPasswd", null)

        val updated = userService.updateUser(uid, updateDTO)
        assertNotNull(updated)
        assertEquals(updateDTO.name, updated!!.name)
        assertEquals(user.id, updated.id)
        assertEquals(user.email, updated.email)
        assertEquals(user.permission, updated.permission)
        assertTrue(passwordEncoder.matches(updateDTO.password, updated.hashedPassword))
    }

    @Test
    @Description("Test Delete User With DB")
    fun deleteUser() {
        assertEquals(userService.deleteUser(user.id!!), true)
        val nUser = transaction {
            Users.selectAll().where { Users.id eq user.id }.count()
        }
        assertEquals(nUser, 0)
    }

    @Test
    @Description("Test Create of User With DB")
    fun createUser() {
        // Test Name Conflict
        var newUserDTO = NewUserDTO(user.name, user.hashedPassword, user.email)
        assertThrows<UserConflictExcept> { userService.createUser(newUserDTO) }


        newUserDTO = NewUserDTO(user.name + "_New", "testPwd", "test@example.com")
        user.hashedPassword = passwordEncoder.encode(newUserDTO.password)
        user.id = 1

        val created = userService.createUser(newUserDTO)

        assertEquals(newUserDTO.name, created.name)
        assertEquals(newUserDTO.email, created.email)
        assertEquals(UserRole.ROLE_READ_ONLY.key, created.permission)

        assertEquals(LocalDateTime.now().month, created.createdAt.month)
        assertTrue(passwordEncoder.matches(newUserDTO.password, created.hashedPassword))

    }

    @Test
    @Description("Test of Get Users")
    fun getUsers() {
        val users = userService.getUsers()
        assertEquals(1, users.size)
    }
}


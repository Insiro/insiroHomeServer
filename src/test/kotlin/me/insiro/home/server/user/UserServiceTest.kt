package me.insiro.home.server.user

import me.insiro.home.server.testUtils.AbsDataBaseTest
import me.insiro.home.server.testUtils.DBInserter
import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import me.insiro.home.server.user.exception.UserConflictExcept
import me.insiro.home.server.user.exception.UserNotFoundException
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
class UserServiceTest : AbsDataBaseTest(Users) {
    private val userRepository = UserRepository()
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    private val userService = UserService(userRepository, passwordEncoder)
    private lateinit var user: User

    @BeforeEach
    fun init() {
        resetDataBase()
        user = DBInserter.insertUser(User("testName", passwordEncoder.encode("testPwd"), "test@example.com", 0b1))
    }

    @Test
    @Description("Test Get User With DataBase")
    fun getUser() {
        val uid = user.id!!.value
        //Return Null when Wrong ID is requested
        assertThrows<UserNotFoundException>{userService.getUser(User.Id(uid + 5)).getOrThrow()}
        //Return User's VO when
        val gUser = userService.getUser(user.id!!).getOrThrow()
        assertEquals(uid, gUser.id!!.value)
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

        val updated = userService.updateUser(uid, updateDTO).getOrNull()
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
            Users.selectAll().where { Users.id eq user.id!!.value }.count()
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

        val created = userService.createUser(newUserDTO)

        assertEquals(newUserDTO.name, created.name)
        assertEquals(newUserDTO.email, created.email)
        assertEquals(UserRole.ROLE_USER.key, created.permission)

        assertEquals(LocalDateTime.now().month, created.createdAt?.month)
        assertTrue(passwordEncoder.matches(newUserDTO.password, created.hashedPassword))

    }

    @Test
    @Description("Test of Get Users")
    fun getUsers() {
        val users = userService.getUsers(null)
        assertEquals(1, users.size)
    }

    @Test
    fun loadUserByUsername() {
        val details = userService.loadUserByUsername(user.name)
        assertNotNull(details)
        assertEquals(user.name, details.username)
        assertEquals(user.hashedPassword, details.password)

        assertEquals(user.name, details.user.name)
        assertEquals(user.email, details.user.email)
        assertEquals(user.id, details.user.id)


    }
}


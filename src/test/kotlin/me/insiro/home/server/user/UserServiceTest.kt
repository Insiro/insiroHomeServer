package me.insiro.home.server.user

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.application.exception.UserNotFoundException
import me.insiro.home.server.testUtils.AbsDataBaseTest
import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import me.insiro.home.server.user.utils.AuthPasswordProvider
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.annotation.Description
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UserServiceTest : AbsDataBaseTest(arrayListOf(Users)) {
    private val userRepository = UserRepository()
    private val passwordEncoder = AuthPasswordProvider()
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

class UserRepository : AbsRepository<Long, User, Users>(Users) {
    override fun new(vo: User): User {
        val id = transaction {
            if (Users.select(Users.name).where { Users.name eq vo.name }.count() != 0L)
                throw UserConflictExcept(vo.name)
            Users.insertAndGetId {
                it[name] = vo.name
                it[email] = vo.email
                it[password] = vo.hashedPassword
                it[permission] = vo.permission
                it[createdAt] = vo.createdAt
            }
        } ?: throw UserInsertFailedException(vo)
        val updated = vo.copy()
        updated.id = id.value
        return updated
    }

    override fun relationObjectMapping(it: ResultRow): User {
        val user = User(it[Users.name], it[Users.password], it[Users.email], it[Users.permission])
        user.id = it[Users.id].value
        return user
    }

    override fun update(vo: User): User = transaction {
        val id = vo.id ?: throw UserNotFoundException(id = null)
        Users.update {
            this.id eq id
            it[permission] = vo.permission
            it[password] = vo.hashedPassword
            it[name] = vo.name
            it[email] = vo.email
        }
        findById(id)!!
    }
}

abstract class QueryFailedException(status: HttpStatus, msg: String) : AbsException(status, msg)
class UserConflictExcept(name: String) : QueryFailedException(HttpStatus.CONFLICT, "User Name Conflict ( ${name})")
class UserInsertFailedException(user: User) : QueryFailedException(HttpStatus.INTERNAL_SERVER_ERROR,
        "Insertion Failed on (name : ${user.name}, password : ${user.hashedPassword}, email : ${user.email})"
)
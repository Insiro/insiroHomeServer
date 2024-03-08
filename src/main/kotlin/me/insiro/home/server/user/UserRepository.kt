package me.insiro.home.server.user

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import me.insiro.home.server.user.exception.UserConflictExcept
import me.insiro.home.server.user.exception.UserNotFoundException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UserRepository : AbsRepository<Long, Users, User, User.Id> {
    override val table = Users
    fun upsertById(vo: User) = transaction {
        assert(vo.id != null)
        val user = Users.selectAll().where { Users.id eq vo.id!!.value }.firstOrNull()
        if (user == null) {
            val now = LocalDateTime.now()
            Users.insert {
                it[id] = vo.id!!.value
                it[name] = vo.name
                it[email] = vo.email
                it[password] = vo.hashedPassword
                it[permission] = vo.permission
                it[createdAt] = now
            }
            vo.copy(createdAt = now)
        } else
            update(vo)
    }

    override fun new(vo: User): User {
        val id = transaction {
            if (Users.select(Users.name).where { Users.name eq vo.name }.count() != 0L)
                throw UserConflictExcept(vo.name)
            Users.insertAndGetId {
                it[name] = vo.name
                it[email] = vo.email
                it[password] = vo.hashedPassword
                it[permission] = vo.permission
                it[createdAt] = LocalDateTime.now()
            }
        }
        val updated = vo.copy(id = User.Id(id))
        return updated
    }

    override fun relationObjectMapping(it: ResultRow): User {
        val user = User(
            it[Users.name],
            it[Users.password],
            it[Users.email],
            it[Users.permission],
            id = User.Id(it[Users.id]),
            it[Users.createdAt]
        )
        return user
    }

    override fun update(vo: User): User = transaction {
        val id = vo.id ?: throw UserNotFoundException(id = null)
        Users.update(where = { Users.id eq id.value }) {
            it[permission] = vo.permission
            it[password] = vo.hashedPassword
            it[name] = vo.name
            it[email] = vo.email
        }
        findById(id)!!
    }

    fun update(
        id: User.Id,
        password: String? = null,
        name: String? = null,
        email: String? = null,
        permission: Int? = null
    ): User? = transaction {
        Users.update(where = { Users.id eq id.value }) {
            password?.apply { it[Users.password] = this }
            name?.apply { it[Users.name] = this }
            email?.apply { it[Users.email] = this }
            permission?.apply { it[Users.permission] = this }
        }
        findById(id)
    }

    fun find(userName: String): User? = transaction {
        val selected = Users.selectAll().where { Users.name eq userName }.firstOrNull()
        selected?.let { relationObjectMapping(it) }
    }
}


package me.insiro.home.server.user

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import me.insiro.home.server.user.exception.UserConflictExcept
import me.insiro.home.server.user.exception.UserNotFoundException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository

@Repository
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
        }
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


package me.insiro.home.server.user

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Repository


@Repository
class UserRepository : AbsRepository<Long, User, Users>(Users) {
    override fun new(vo: User): User {
        TODO("Not yet implemented")
    }

    override fun relationObjectMapping(it: ResultRow): User {
        val user = User(it[Users.name], it[Users.password], it[Users.email], it[Users.permission])
        user.id = it[Users.id].value
        return user
    }

    override fun update(vo: User): User {
        TODO("Not yet implemented")
    }


}


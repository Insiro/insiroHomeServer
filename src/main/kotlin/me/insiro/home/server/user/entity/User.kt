package me.insiro.home.server.user.entity

import me.insiro.home.server.application.domain.BaseIDTable
import me.insiro.home.server.application.domain.EntityVO
import org.jetbrains.exposed.sql.Column


object Users : BaseIDTable() {
    val name: Column<String> = varchar("nick_name", 20)
    val password: Column<String> = varchar(name = "pwd", length = 50)
    val email: Column<String> = varchar("email", 50)
    val permission: Column<Int> = integer("permission")
}


data class User(
        var name: String,
        var hashedPassword: String,
        var email: String,
        var permission: Int,
) : EntityVO<Long>()
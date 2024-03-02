package me.insiro.home.server.user.entity

import me.insiro.home.server.application.domain.BaseEntityVO
import me.insiro.home.server.application.domain.BaseIDTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column


object Users : BaseIDTable() {
    val name: Column<String> = varchar("nick_name", 20).uniqueIndex()
    val password: Column<String> = varchar(name = "pwd", length = 100)
    val email: Column<String> = varchar("email", 50)
    val permission: Column<Int> = integer("permission")
}


data class User(
    var name: String,
    var hashedPassword: String,
    var email: String,
    var permission: Int,
    override val id: Id? = null,
) : BaseEntityVO() {
    @JvmInline
    value class Id(override val value: Long) : BaseEntityVO.Id {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}
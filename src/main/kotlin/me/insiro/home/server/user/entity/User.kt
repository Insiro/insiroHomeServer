package me.insiro.home.server.user.entity

import kotlinx.serialization.Serializable
import me.insiro.home.server.application.domain.BaseIDTable
import me.insiro.home.server.application.domain.IBaseEntityID
import me.insiro.home.server.application.domain.IBaseEntityVO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import java.time.LocalDateTime


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
    override val createdAt: LocalDateTime?=null,
) : IBaseEntityVO {
    @JvmInline
    @Serializable
    value class Id(override val value: Long) : IBaseEntityID {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}
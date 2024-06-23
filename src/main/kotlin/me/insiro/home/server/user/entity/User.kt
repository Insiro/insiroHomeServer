package me.insiro.home.server.user.entity

import kotlinx.serialization.Serializable
import me.insiro.home.server.application.domain.entity.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime


object Users : LongIdTable(), TableCreatedAt {
    val name: Column<String> = varchar("nick_name", 20).uniqueIndex()
    val password: Column<String> = varchar(name = "pwd", length = 100)
    val email: Column<String> = varchar("email", 50)
    val permission: Column<Int> = integer("permission")
    override val createdAt = datetime("createdAt").clientDefault{LocalDateTime.now()}
}


data class User(
    var name: String,
    var hashedPassword: String,
    var email: String,
    var permission: Int,
    override val id: Id? = null,
    override val createdAt: LocalDateTime? = null,
) : LongEntityVO, ICreatedAt {
    @JvmInline
    @Serializable
    value class Id(override val value: Long) : LongID {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}
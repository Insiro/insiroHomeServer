package me.insiro.home.server.application.domain.entity

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*


interface IEntityVO<ID : Comparable<ID>> {
    val id: Id<ID>?

    interface Id<ID> {
        val value: ID
    }
}

interface ICreatedAt{
    val createdAt: LocalDateTime?
}
interface TableCreatedAt{
    val createdAt: Column<LocalDateTime>
}

typealias IntEntityVO = IEntityVO<Int>
typealias LongEntityVO = IEntityVO<Long>
typealias UUIDEntityVO = IEntityVO<UUID>

abstract class LongBaseTable :  LongIdTable() , TableCreatedAt{
    override val createdAt = datetime("createdAt").clientDefault{LocalDateTime.now()}
}

abstract class UUIDBaseTable : UUIDTable() , TableCreatedAt{
    override val createdAt = datetime("createdAt").clientDefault { LocalDateTime.now() }
}

abstract class IntBaseTable :  IntIdTable(), TableCreatedAt {
    override val createdAt = datetime("createdAt").clientDefault { LocalDateTime.now() }
}

typealias IntID = IEntityVO.Id<Int>
typealias LongID = IEntityVO.Id<Long>
typealias VoUUID = IEntityVO.Id<UUID>


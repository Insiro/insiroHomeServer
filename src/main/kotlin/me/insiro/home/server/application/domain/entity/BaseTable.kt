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

    val createdAt: LocalDateTime?

    interface Id<ID> {
        val value: ID
    }
}
typealias IntEntityVO = IEntityVO<Int>
typealias LongEntityVO = IEntityVO<Long>
typealias UUIDEntityVO = IEntityVO<UUID>

interface IBaseTable<ID : Comparable<ID>> {
    val createdAt: Column<LocalDateTime>
}

abstract class LongBaseTable : IBaseTable<Long>, LongIdTable() {
    override val createdAt = datetime("createdAt").clientDefault { LocalDateTime.now() }
}

abstract class UUIDBaseTable : IBaseTable<UUID>, UUIDTable() {
    override val createdAt = datetime("createdAt").clientDefault { LocalDateTime.now() }
}

abstract class IntBaseTable : IBaseTable<Int>, IntIdTable() {
    override val createdAt = datetime("createdAt").clientDefault { LocalDateTime.now() }
}

typealias IntID = IEntityVO.Id<Int>
typealias LongID = IEntityVO.Id<Long>
typealias VoUUID = IEntityVO.Id<UUID>


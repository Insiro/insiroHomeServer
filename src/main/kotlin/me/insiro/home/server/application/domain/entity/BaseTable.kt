package me.insiro.home.server.application.domain.entity

import org.jetbrains.exposed.sql.Column
import java.time.LocalDateTime
import java.util.*


interface IEntityVO<ID : Comparable<ID>> {
    val id: Id<ID>?

    interface Id<ID> {
        val value: ID
    }
}

interface ICreatedAt {
    val createdAt: LocalDateTime?
}

interface TableCreatedAt {
    val createdAt: Column<LocalDateTime>
}

typealias IntEntityVO = IEntityVO<Int>
typealias LongEntityVO = IEntityVO<Long>
typealias UUIDEntityVO = IEntityVO<UUID>

typealias IntID = IEntityVO.Id<Int>
typealias LongID = IEntityVO.Id<Long>
typealias VoUUID = IEntityVO.Id<UUID>


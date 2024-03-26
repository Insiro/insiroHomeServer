package me.insiro.home.server.application.domain

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

abstract class BaseIDTable : LongIdTable() {
    val createdAt: Column<LocalDateTime> = datetime("createdAt").clientDefault { LocalDateTime.now() }
}
typealias IBaseEntityVO = IEntityVO<Long>

typealias IBaseEntityID =  IEntityVO.Id<Long>
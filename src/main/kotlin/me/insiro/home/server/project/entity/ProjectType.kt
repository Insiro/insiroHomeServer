package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.IEntityVO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object ProjectTypes : IntIdTable() {
    val name = varchar("name", 100) .uniqueIndex()
    val createdAt: Column<LocalDateTime> = datetime("createdAt").clientDefault { LocalDateTime.now() }
}


data class ProjectType(
    var name: String,
    override val id: IEntityVO.Id<Int>?,
    override val createdAt: LocalDateTime? = null,
) : IEntityVO<Int> {
    @JvmInline
    value class Id(override val value: Int) : IEntityVO.Id<Int> {
        constructor(entityID: EntityID<Int>) : this(entityID.value)
    }
}



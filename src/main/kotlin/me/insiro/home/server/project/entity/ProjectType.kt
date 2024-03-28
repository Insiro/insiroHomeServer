package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.entity.IntBaseTable
import me.insiro.home.server.application.domain.entity.IntEntityVO
import me.insiro.home.server.application.domain.entity.IntID
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

object ProjectTypes : IntBaseTable(){
    val name = varchar("name", 100) .uniqueIndex()
}


data class ProjectType(
    var name: String,
    override val id: Id?=null,
    override val createdAt: LocalDateTime? = null,
) : IntEntityVO {
    @JvmInline
    value class Id(override val value: Int) : IntID{
        constructor(entityID: EntityID<Int>) : this(entityID.value)
    }
}



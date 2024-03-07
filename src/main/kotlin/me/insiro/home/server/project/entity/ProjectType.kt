package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.IEntityVO
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime


data class ProjectType(
    var name: String,
    override val id: IEntityVO.Id<Int>?,
    override val createdAt: LocalDateTime?=null,
) : IEntityVO<Int> {
    @JvmInline
    value class Id(override val value: Int) : IEntityVO.Id<Int> {
        constructor(entityID: EntityID<Int>) : this(entityID.value)
    }
}



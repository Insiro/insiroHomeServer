package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.BaseEntityVO
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

data class Project(
    var title: String, var status: String, var fileName: String,
    override val id: Id? = null,
    override val createdAt: LocalDateTime?=null,
) : BaseEntityVO() {
    @JvmInline
    value class Id(override val value: Long) : BaseEntityVO.Id {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}

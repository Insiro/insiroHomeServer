package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.IBaseEntityVO
import me.insiro.home.server.application.domain.TitledVO
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

data class Project(
    override var title: String, var status: String, var fileName: String,
    override val id: Id? = null,
    override val createdAt: LocalDateTime? = null,
) : TitledVO {
    @JvmInline
    value class Id(override val value: Long) : IBaseEntityVO.Id {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}

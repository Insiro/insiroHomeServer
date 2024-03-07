package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.EntityVO
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime


data class Language(
    var name: String,
    override val id: EntityVO.Id<Int>?,
    override val createdAt: LocalDateTime?=null,
) : EntityVO<Int>() {
    @JvmInline
    value class Id(override val value: Int) : EntityVO.Id<Int> {
        constructor(entityID: EntityID<Int>) : this(entityID.value)
    }
}




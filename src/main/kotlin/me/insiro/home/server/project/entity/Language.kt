package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.EntityVO
import org.jetbrains.exposed.dao.id.EntityID


data class Language(
    var name: String,
    override val id: EntityVO.Id<Int>?,
) : EntityVO<Int>() {
    @JvmInline
    value class Id(override val value: Int) : EntityVO.Id<Int> {
        constructor(entityID: EntityID<Int>) : this(entityID.value)
    }
}




package me.insiro.home.server.post.entity

import me.insiro.home.server.application.domain.EntityVO
import org.jetbrains.exposed.dao.id.EntityID

data class Category(
    val name: String,
    override val id: Id? = null,
) : EntityVO<Int>() {
    @JvmInline
    value class Id(override val value: Int) : EntityVO.Id<Int> {
        constructor(entityID: EntityID<Int>) : this(entityID.value)
    }

}

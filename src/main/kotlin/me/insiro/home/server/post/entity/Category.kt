package me.insiro.home.server.post.entity

import kotlinx.serialization.Serializable
import me.insiro.home.server.application.domain.entity.IntEntityVO
import me.insiro.home.server.application.domain.entity.IntID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Categories : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
}

data class Category(
    val name: String,
    override val id: Id? = null,
) : IntEntityVO {
    @JvmInline
    @Serializable
    value class Id(override val value: Int) : IntID {
        constructor(entityID: EntityID<Int>) : this(entityID.value)
    }
}

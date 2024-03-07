package me.insiro.home.server.post.entity

import kotlinx.serialization.Serializable
import me.insiro.home.server.application.domain.IEntityVO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import java.time.LocalDateTime

object Categories : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
}

data class Category(
    val name: String,
    override val id: Id? = null,
    override val createdAt: LocalDateTime? = null,
) : IEntityVO<Int> {
    @JvmInline
    @Serializable
    value class Id(override val value: Int) : IEntityVO.Id<Int> {
        constructor(entityID: EntityID<Int>) : this(entityID.value)
    }

}

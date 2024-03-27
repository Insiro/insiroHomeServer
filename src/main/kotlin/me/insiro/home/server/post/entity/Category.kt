package me.insiro.home.server.post.entity

import kotlinx.serialization.Serializable
import me.insiro.home.server.application.domain.entity.IntBaseTable
import me.insiro.home.server.application.domain.entity.IntEntityVO
import me.insiro.home.server.application.domain.entity.IntID
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

object Categories : IntBaseTable() {
    val name = varchar("name", 50).uniqueIndex()
}

data class Category(
    val name: String,
    override val id: Id? = null,
    override val createdAt: LocalDateTime? = null
) : IntEntityVO {
    @JvmInline
    @Serializable
    value class Id(override val value: Int) : IntID {
        constructor(entityID: EntityID<Int>) : this(entityID.value)
    }

}

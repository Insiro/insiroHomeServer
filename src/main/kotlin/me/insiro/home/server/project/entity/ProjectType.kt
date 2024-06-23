package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.entity.IntEntityVO
import me.insiro.home.server.application.domain.entity.IntID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ProjectTypes : IntIdTable() {
    val name = varchar("name", 100).uniqueIndex()
    val isLang = bool("is_lang").default(false)
}


data class ProjectType(
    var name: String,
    var isLang: Boolean = false,
    override val id: Id? = null,
) : IntEntityVO {
    @JvmInline
    value class Id(override val value: Int) : IntID {
        constructor(entityID: EntityID<Int>) : this(entityID.value)
    }
}



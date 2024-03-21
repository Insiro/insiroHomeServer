package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.IBaseEntityID
import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.application.domain.TitledTable
import me.insiro.home.server.application.domain.TitledVO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import java.time.LocalDateTime

object Projects : TitledTable() {
    val status = enumeration<Status>("status")
}

object ProjectTypeRelations : Table() {
    val typeId = reference("typeId", ProjectTypes.id, onDelete = ReferenceOption.CASCADE)
    val projectId = reference("projectId", Projects.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey: PrimaryKey = PrimaryKey(typeId, projectId, name = "typeId_projectId")
}

sealed interface Project : TitledVO {
    val status: String

    data class Raw(
        override val id: Id? = null,
        override var title: String,
        override var status: String,
        override val createdAt: LocalDateTime? = null,
    ) : Project

    data class Joined(
        override val id: Id? = null,
        override var title: String,
        override var status: String,
        val types: List<ProjectType>? = null,
        override val createdAt: LocalDateTime? = null,
    ) : Project

    @JvmInline
    value class Id(override val value: Long) : IBaseEntityID {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}


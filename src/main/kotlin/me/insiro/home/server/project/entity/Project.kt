package me.insiro.home.server.project.entity

import me.insiro.home.server.application.domain.entity.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Projects : LongIdTable(), ITitledTable, TableCreatedAt {
    val status = enumeration<Status>("status")
    override val title = varchar("title", 100)
    override val createdAt = datetime("createdAt").clientDefault { LocalDateTime.now() }
}

object ProjectTypeRelations : Table() {
    val typeId = reference("typeId", ProjectTypes.id, onDelete = ReferenceOption.CASCADE)
    val projectId = reference("projectId", Projects.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey: PrimaryKey = PrimaryKey(typeId, projectId, name = "typeId_projectId")
}

sealed interface Project : LongEntityVO, TitledVO, ICreatedAt {
    val status: Status
    override val id: Id?

    @JvmInline
    value class Id(override val value: Long) : LongID {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }

    data class Raw(
        override var title: String,
        override var status: Status = Status.PUBLISHED,
        override val id: Id? = null,
        override val createdAt: LocalDateTime? = null,
    ) : Project

    data class Joined(
        override var title: String,
        override var status: Status = Status.PUBLISHED,
        override val id: Id? = null,
        val types: List<ProjectType>? = null,
        override val createdAt: LocalDateTime? = null,
    ) : Project {
        constructor(project: Project, types: List<ProjectType>? = null) : this(
            project.title,
            project.status,
            project.id,
            types,
            project.createdAt
        )

    }
}


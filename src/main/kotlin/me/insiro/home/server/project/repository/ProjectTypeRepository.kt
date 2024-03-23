package me.insiro.home.server.project.repository

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.entity.ProjectType
import me.insiro.home.server.project.entity.ProjectTypeRelations
import me.insiro.home.server.project.entity.ProjectTypes
import me.insiro.home.server.project.exception.ProjectTypeNotFoundException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ProjectTypeRepository : AbsRepository<Int, ProjectTypes, ProjectType, ProjectType.Id> {
    override val table = ProjectTypes

    override fun relationObjectMapping(it: ResultRow): ProjectType {
        return ProjectType(it[table.name], ProjectType.Id(it[table.id]), it[table.createdAt])
    }

    override fun update(vo: ProjectType): ProjectType {
        ProjectTypes.update(where = { ProjectTypes.id eq vo.id!!.value }) { it[name] = vo.name }
        return vo
    }

    override fun new(vo: ProjectType): ProjectType = transaction {
        val now = LocalDateTime.now()
        val id = ProjectTypes.insertAndGetId {
            it[name] = vo.name
            it[createdAt] = now
        }
        vo.copy(id = ProjectType.Id(id), createdAt = now)
    }

    fun get(projectName: String): Result<ProjectType> = transaction {
        ProjectTypes
            .selectAll()
            .where { ProjectTypes.name eq projectName }
            .firstOrNull()
            ?.let { Result.success(relationObjectMapping(it)) }
            ?: Result.failure(ProjectTypeNotFoundException(projectName))
    }

    fun find(projectId: Project.Id): List<ProjectType> = transaction {
        ProjectTypeRelations
            .join(ProjectTypes, JoinType.LEFT, onColumn = ProjectTypeRelations.typeId, otherColumn = ProjectTypes.id)
            .select(ProjectTypes.fields)
            .where { ProjectTypeRelations.projectId eq projectId.value }
            .map { relationObjectMapping(it) }
    }

    fun delete(projectId: Project.Id): Int = transaction {
        ProjectTypeRelations.deleteWhere { ProjectTypeRelations.projectId eq projectId.value }
    }

    fun addRelationOrInsert(projectId: Project.Id, typeName: String): ProjectType = transaction {
        val type = get(typeName).getOrNull() ?: ProjectType(
            typeName,
            ProjectType.Id(ProjectTypes.insertAndGetId { it[name] = typeName })
        )
        ProjectTypeRelations.insert {
            it[this.projectId] = projectId.value
            it[this.typeId] = type.id!!.value
        }
        type
    }
}
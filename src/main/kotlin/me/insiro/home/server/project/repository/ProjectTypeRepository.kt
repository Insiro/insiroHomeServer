package me.insiro.home.server.project.repository

import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.project.entity.ProjectType
import me.insiro.home.server.project.entity.ProjectTypes
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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
}
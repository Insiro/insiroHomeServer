package me.insiro.home.server.project.repository


import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.entity.Projects
import me.insiro.home.server.project.exception.ProjectNotFoundException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class ProjectRepository : AbsRepository<UUID, Projects, Project, Project.Id> {
    override val table = Projects

    override fun relationObjectMapping(it: ResultRow): Project.Raw {
        return Project.Raw(it[table.title], it[table.status], Project.Id(it[table.id].value), it[table.createdAt])
    }

    override fun update(vo: Project): Project.Raw = transaction {
        assert(vo.id != null)
        Projects.update(where = { Projects.id eq vo.id!!.value }) {
            it[title] = vo.title
            it[status] = vo.status
        }
        vo as Project.Raw
    }

    fun update(id: Project.Id, title: String? = null, status: Status? = null): Project = transaction {
        Projects.update(where = { Projects.id eq id.value }) {
            title?.let { title -> it[Projects.title] = title }
            status?.let { status -> it[Projects.status] = status }
        }
        findById(id) ?: throw ProjectNotFoundException(id)
    }

    override fun new(vo: Project): Project.Raw = transaction {
        val now = LocalDateTime.now()
        val id = Projects.insertAndGetId {
            it[title] = vo.title
            it[createdAt] = now
            it[status] = vo.status
        }
        (vo as Project.Raw).copy(id = Project.Id(id), createdAt = now)
    }

    fun new(vo: Project, id: Project.Id): Project.Raw? = transaction {
        findById(id)?.apply { return@transaction null }
        val now = LocalDateTime.now()
        Projects.insert {
            it[title] = vo.title
            it[createdAt] = now
            it[status] = vo.status
            it[Projects.id] = id.value
        }
        (vo as Project.Raw).copy(id = id, createdAt = now)
    }

    fun find(filterOption: List<Status>? = null, offsetLimit: OffsetLimit? = null): List<Project.Raw> =
        transaction {
            val query = Projects.selectAll()
            filterOption?.let { it.forEach { query.adjustWhere { Projects.status eq it } } }
            offsetLimit?.let { query.limit(offsetLimit.limit, offsetLimit.offset) }
            query.map(::relationObjectMapping)
        }
}
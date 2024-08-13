package me.insiro.home.server.project.repository


import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.entity.Projects
import me.insiro.home.server.project.exception.ProjectNotFoundException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ProjectRepository : AbsRepository<Long, Projects, Project, Project.Id> {
    override val table = Projects

    override fun relationObjectMapping(it: ResultRow): Project.Raw {
        return Project.Raw(it[table.title], it[table.status], Project.Id(it[table.id].value), it[table.createdAt])
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

    fun findByTitle(title: String): Result<Project.Raw> = transaction {
        try {
            val project = Projects.selectAll().where { Projects.title eq title }.first()
            Result.success(relationObjectMapping(project))
        } catch (e: NoSuchElementException) {
            Result.failure(ProjectNotFoundException(title))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun find(filterOption: List<Status>? = null, offsetLimit: OffsetLimit? = null, keywords:String?=null): List<Project.Raw> =
        transaction {
            val query = Projects.selectAll()
            filterOption?.let { it.forEach { query.adjustWhere { Projects.status eq it } } }
            offsetLimit?.apply { query.limit(offsetLimit.limit, offsetLimit.offset) }
            keywords?.apply{ query.adjustWhere { Projects.title like "%$keywords%" } }
            query.map(::relationObjectMapping)
        }

    fun update(title: String, newTitle: String? = null, status: Status? = null): Result<Project> = transaction {
        Projects.update(where = { Projects.title eq title }) {
            newTitle?.let { title -> it[this.title] = title }
            status?.let { status -> it[this.status] = status }
        }
        if (newTitle != null) findByTitle(newTitle)
        else findByTitle(title)
    }

    override fun update(vo: Project): Project.Raw = transaction {
        assert(vo.id != null)
        Projects.update(where = { Projects.id eq vo.id!!.value }) {
            it[title] = vo.title
            it[status] = vo.status
        }
        vo as Project.Raw
    }

    fun update(title: String, vo: Project): Project.Raw = transaction {
        Projects.update(where = { Projects.title eq title }) {
            it[this.title] = vo.title
            it[status] = vo.status
        }
        vo as Project.Raw
    }


    fun delete(title: String): Boolean = transaction {
        0 != Projects.deleteWhere { this.title eq title }
    }

}
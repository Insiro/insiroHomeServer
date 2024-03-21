package me.insiro.home.server.project.repository


import me.insiro.home.server.application.AbsRepository
import me.insiro.home.server.project.entity.Project
import me.insiro.home.server.project.entity.Projects
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
@Repository
class ProjectRepository : AbsRepository<Long, Projects, Project, Project.Id> {
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

    override fun new(vo: Project): Project.Raw = transaction {
        val now = LocalDateTime.now()
        val id = Projects.insertAndGetId {
            it[title] = vo.title
            it[createdAt] = now
            it[status] = vo.status
        }
        (vo as Project.Raw).copy(id = Project.Id(id), createdAt = now)
    }

}
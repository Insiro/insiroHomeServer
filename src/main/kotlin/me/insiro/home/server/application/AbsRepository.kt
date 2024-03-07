package me.insiro.home.server.application

import me.insiro.home.server.application.domain.OffsetLimit
import me.insiro.home.server.application.domain.IEntityVO
import org.jetbrains.exposed.dao.DaoEntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction

interface AbsRepository<Id : Comparable<Id>, Table, VO, VoId> where   VO : IEntityVO<Id>, Table : IdTable<Id>, VoId : IEntityVO.Id<Id> {
    val table: Table
    fun relationObjectMapping(it: ResultRow): VO
    fun findById(id: VoId): VO? = transaction {
        val result = table.selectAll().where {
            table.id eq DaoEntityID(id.value, table)
        }.limit(1).map { relationObjectMapping(it) }
        val ret = if (result.isEmpty()) null else result[0]
        ret
    }

    fun find(limitOption: OffsetLimit?=null): List<VO> = transaction {
        table.selectAll()
            .let { query -> limitOption?.let { query.limit(it.limit, it.offset) } ?: query }
            .map { relationObjectMapping(it) }
    }

    private fun find(op: SqlExpressionBuilder.() -> Op<Boolean>): List<VO> = transaction {
        table.selectAll().where(op).map { relationObjectMapping(it) }
    }

    fun new(vo: VO): VO

    fun update(vo: VO): VO

    private fun update(where: (SqlExpressionBuilder.() -> Op<Boolean>)? = null, body: Table.(UpdateStatement) -> Unit): Int =
        transaction {
            table.update(where, body = body)
        }

    fun update(id: VoId, body: Table.(UpdateStatement) -> Unit): VO? = transaction {
        val ex: SqlExpressionBuilder.() -> Op<Boolean> = { table.id.eq(DaoEntityID(id.value, table)) }
        val nUpdated = table.update(ex, body = body)
        if (nUpdated == 0) return@transaction null
        findById(id)
    }

    private fun delete(id: Id): Boolean = transaction {
        0 != table.deleteWhere { this.id eq DaoEntityID(id, table) }
    }

    fun delete(id: VoId): Boolean {
        return delete(id.value)
    }

    fun delete(vo: VO): Boolean {
        return vo.id?.let { delete(it.value) } ?: false
    }

    private fun delete(op: Table.(ISqlExpressionBuilder) -> Op<Boolean>): Int = transaction {
        table.deleteWhere(op = op)
    }
}
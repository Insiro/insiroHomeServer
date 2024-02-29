package me.insiro.home.server.application

import me.insiro.home.server.application.domain.EntityVO
import org.jetbrains.exposed.dao.DaoEntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction

abstract class AbsRepository<Id, VO, Table>(private val table: Table) where  Id : Comparable<Id>, VO : EntityVO<Id>, Table : IdTable<Id> {
    protected abstract fun relationObjectMapping(it: ResultRow): VO
    fun findById(id: Id): VO? = transaction {
        val result = table.selectAll().where {
            table.id eq DaoEntityID(id, table)
        }.limit(1).map { relationObjectMapping(it) }
        val ret = if (result.isEmpty()) null else result[0]
        ret
    }

    fun find(limit: Int = 0, offset: Long?=null): List<VO> = transaction {
        table.selectAll()
                .let{query-> offset?.let{query.limit(limit, offset)}?:query}
                .map { relationObjectMapping(it) }
    }

    fun find(op: SqlExpressionBuilder.() -> Op<Boolean>): List<VO> = transaction {
        table.selectAll().where(op).map { relationObjectMapping(it) }
    }

    abstract fun new(vo: VO): VO

    abstract fun update(vo: VO): VO

    fun update(where: (SqlExpressionBuilder.() -> Op<Boolean>)? = null, body: Table.(UpdateStatement) -> Unit): Int = transaction {
        table.update(where, body = body)
    }

    fun update(id: Id, body: Table.(UpdateStatement) -> Unit): VO? = transaction {
        val ex: SqlExpressionBuilder.() -> Op<Boolean> = { table.id.eq(DaoEntityID(id, table)) }
        val nUpdated = table.update(ex, body = body)
        if (nUpdated==0) return@transaction null
        findById(id)
    }

    fun deleteById(id: Id): Boolean= transaction {
        0 != table.deleteWhere { this.id eq DaoEntityID(id, table) }
    }

    fun delete(vo: VO): Boolean {
        return vo.id?.let { deleteById(it) } ?: false
    }

    fun delete(op: Table.(ISqlExpressionBuilder) -> Op<Boolean>): Int = transaction{
        table.deleteWhere(op = op)
    }
}
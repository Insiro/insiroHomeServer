package me.insiro.home.server.application

import me.insiro.home.server.application.domain.EntityVO
import org.jetbrains.exposed.dao.DaoEntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction

abstract class AbsRepository<Id, VO, Table>(private val table: Table) where  Id : Comparable<Id>, VO : EntityVO<Id>, Table : IdTable<Id> {
    abstract fun new(vo: VO):VO
    fun findById(id: Id): VO? = transaction {
        val result = table.selectAll().where {
            table.id eq DaoEntityID(id, table)
        }.limit(1).map { relationObjectMapping(it) }
        val ret = if (result.isEmpty())null else result[0]
        ret
    }

    fun find(): List<VO> = transaction { table.selectAll().map { relationObjectMapping(it) } }
    protected abstract fun relationObjectMapping(it: ResultRow): VO
    fun find(op: SqlExpressionBuilder.() -> Op<Boolean>): List<VO> = transaction {
        table.selectAll().where(op).map { relationObjectMapping(it) }
    }

    fun update(where: (SqlExpressionBuilder.() -> Op<Boolean>)? = null, body: Table.(UpdateStatement) -> Unit): Int = transaction {
        table.update(where, body = body)
    }
    abstract fun update(vo: VO): VO

}
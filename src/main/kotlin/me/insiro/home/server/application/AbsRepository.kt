package me.insiro.home.server.application

import me.insiro.home.server.application.domain.EntityVO
import org.springframework.data.relational.core.sql.Where

abstract class AbsRepository<Id, VO> where  Id : Comparable<*>, VO : EntityVO<Id> {
    abstract fun save(vo: VO): VO?
    abstract fun findById(id: Id): VO?

    abstract fun find(ex: Where): List<VO>
    abstract fun find(): List<VO>
}
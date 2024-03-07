package me.insiro.home.server.application.domain

import java.time.LocalDateTime

abstract class EntityVO<ID : Comparable<ID>> {
    abstract val id: Id<ID>?

    abstract val createdAt: LocalDateTime?

    interface Id<ID> {
        val value: ID
    }
}

abstract class BaseEntityVO : EntityVO<Long>() {
    interface Id : EntityVO.Id<Long>
}

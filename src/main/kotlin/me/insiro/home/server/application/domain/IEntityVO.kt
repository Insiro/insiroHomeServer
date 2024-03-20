package me.insiro.home.server.application.domain

import java.time.LocalDateTime

interface IEntityVO<ID : Comparable<ID>> {
    val id: Id<ID>?

    val createdAt: LocalDateTime?

    interface Id<ID> {
        val value: ID
    }
}

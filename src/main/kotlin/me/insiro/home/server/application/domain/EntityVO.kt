package me.insiro.home.server.application.domain

import java.time.LocalDateTime

abstract class EntityVO<T> where T : Comparable<*> {
    var id: T? = null
    var createdAt: LocalDateTime = LocalDateTime.now()

}
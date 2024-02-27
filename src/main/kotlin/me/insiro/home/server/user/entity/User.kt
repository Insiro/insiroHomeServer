package me.insiro.home.server.user.entity

import me.insiro.home.server.application.domain.EntityVO


data class User(
        var name: String,
        var password: String,
        var email: String,
        var writable: Int,
) : EntityVO<Long>()
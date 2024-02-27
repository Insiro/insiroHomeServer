package me.insiro.home.server.post.entity

import me.insiro.home.server.application.domain.EntityVO

data class Category(
        val name: String,
) : EntityVO<Int>()

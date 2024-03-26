package me.insiro.home.server.user.dto

import me.insiro.home.server.user.entity.User

data class SimpleUserDTO(
    val id: Long,
    val name: String
) {
    constructor(user: User):this(
        user.id!!.value, user.name
    )
}
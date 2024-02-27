package me.insiro.home.server.user.dto

data class NewUserDTO(
        val name: String,
        val password: String,
        val email: String,
)
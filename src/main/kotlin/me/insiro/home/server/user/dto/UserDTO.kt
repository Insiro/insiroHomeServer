package me.insiro.home.server.user.dto

data class UserDTO(
        val id: Long,
        val name: String,
        val role: List<UserRole>,
        val email: String,
)

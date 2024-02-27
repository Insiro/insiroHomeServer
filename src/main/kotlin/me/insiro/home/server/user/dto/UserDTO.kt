package me.insiro.home.server.user.dto

import me.insiro.home.server.user.entity.User

data class UserDTO(
        val id: Long,
        val name: String,
        val role: List<UserRole>,
        val email: String,
) {
    companion object {
        fun fromUser(user: User): UserDTO {
            return UserDTO(
                    user.id!!,
                    user.name,
                    UserRole.fromPermissionKey(user.permission),
                    user.email,
            )
        }
    }
}

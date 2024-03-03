package me.insiro.home.server.user.dto

import me.insiro.home.server.user.entity.User
import java.time.LocalDateTime

data class UserDTO(
        val id: Long,
        val name: String,
        val role: List<UserRole>,
        val email: String,
        val createdAt:LocalDateTime
) {
    companion object {
        fun fromUser(user: User): UserDTO {
            return UserDTO(
                    user.id!!.value,
                    user.name,
                    UserRole.fromPermissionKey(user.permission),
                    user.email,
                    user.createdAt
            )
        }
    }
}

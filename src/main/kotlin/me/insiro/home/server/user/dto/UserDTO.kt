package me.insiro.home.server.user.dto

import me.insiro.home.server.application.domain.dto.IResponseDTO
import me.insiro.home.server.user.entity.User
import java.time.LocalDateTime

data class UserDTO(
    override val id: Long,
    val name: String,
    val role: List<UserRole>,
    val email: String,
    override val createdAt: LocalDateTime
) : IResponseDTO<Long> {
    companion object {
        fun fromUser(user: User): UserDTO {
            assert(user.createdAt != null)
            return UserDTO(
                user.id!!.value,
                user.name,
                UserRole.fromPermissionKey(user.permission),
                user.email,
                user.createdAt!!
            )
        }
    }
}

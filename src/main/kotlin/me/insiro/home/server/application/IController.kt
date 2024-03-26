package me.insiro.home.server.application

import me.insiro.home.server.application.exception.UnAuthorizedException
import me.insiro.home.server.user.dto.AuthDetail
import me.insiro.home.server.user.entity.User
import org.springframework.security.core.context.SecurityContextHolder

interface IController {
    fun getSignedUser(): Result<User> {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal
            if (principal is AuthDetail)
                return Result.success( principal.user)
        }
        return Result.failure(UnAuthorizedException())
    }
}
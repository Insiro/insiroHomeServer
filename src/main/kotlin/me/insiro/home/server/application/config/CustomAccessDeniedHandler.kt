package me.insiro.home.server.application.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.AccessDeniedHandler

class CustomAccessDeniedHandler :AccessDeniedHandler{
    override fun handle(request: HttpServletRequest?, response: HttpServletResponse?, accessDeniedException: AccessDeniedException?) {
        response?: return
        val context = SecurityContextHolder.getContext()
        if (!context.authentication.isAuthenticated )
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED )
        response.sendError(HttpServletResponse.SC_FORBIDDEN)
    }
}
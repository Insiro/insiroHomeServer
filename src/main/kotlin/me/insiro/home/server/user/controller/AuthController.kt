package me.insiro.home.server.user.controller

import jakarta.servlet.http.HttpServletRequest
import me.insiro.home.server.application.IController
import me.insiro.home.server.user.dto.AuthDetail
import me.insiro.home.server.user.dto.SignInDTO
import me.insiro.home.server.user.dto.UserDTO
import me.insiro.home.server.user.utils.AuthenticateProvider
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION
import org.springframework.web.context.request.RequestContextHolder


@RestController
@RequestMapping("auth")
class AuthController(private val authenticateProvider: AuthenticateProvider) : IController {
    @GetMapping
    fun getSignedUserInfo(): ResponseEntity<*> {
        val user = getSignedUser() ?: ResponseEntity("Not Authenticated", HttpStatus.UNAUTHORIZED)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PostMapping
    fun singIn(@RequestBody signInDTO: SignInDTO): ResponseEntity<UserDTO> {
        val authentication = authenticateProvider.authenticate(
            UsernamePasswordAuthenticationToken(signInDTO.name, signInDTO.password)
        )
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        RequestContextHolder.currentRequestAttributes()
            .setAttribute(SPRING_SECURITY_CONTEXT_KEY, context, SCOPE_SESSION)
        SecurityContextHolder.setContext(context)
        val detail = authentication.principal as AuthDetail
        return ResponseEntity(UserDTO.fromUser(detail.user), HttpStatus.OK)
    }

    @DeleteMapping
    fun singOut(request: HttpServletRequest): String {
        RequestContextHolder.currentRequestAttributes().removeAttribute(SPRING_SECURITY_CONTEXT_KEY, SCOPE_SESSION)
        SecurityContextHolder.clearContext()
        return "sign out"
    }
}
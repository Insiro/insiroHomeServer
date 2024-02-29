package me.insiro.home.server.user.controller

import jakarta.servlet.http.HttpServletRequest
import me.insiro.home.server.user.dto.AuthDetail
import me.insiro.home.server.user.dto.SignInDTO
import me.insiro.home.server.user.dto.UserDTO
import me.insiro.home.server.user.utils.AuthenticateProvider
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("auth")
class AuthController(private val authenticateProvider: AuthenticateProvider) {
    @GetMapping
    fun getSignedUser(): ResponseEntity<*> {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal
            if (principal is AuthDetail) {
                val user = UserDTO.fromUser(principal.user)
                return ResponseEntity(user, HttpStatus.OK)
            }
        }
        return ResponseEntity("Not Authenticated", HttpStatus.UNAUTHORIZED)
    }

    @PostMapping
    fun singIn(@RequestBody signInDTO: SignInDTO, request: HttpServletRequest): ResponseEntity<UserDTO> {
        val authentication = authenticateProvider.authenticate(
                UsernamePasswordAuthenticationToken(
                        signInDTO.name,
                        signInDTO.password
                )
        )
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        SecurityContextHolder.setContext(context)
        val detail = authentication.principal as AuthDetail
        return ResponseEntity(UserDTO.fromUser(detail.user), HttpStatus.OK)
    }

    @DeleteMapping
    fun singOut() {
        TODO("Not yet implemented")
    }
}
package me.insiro.home.server.user.utils

import me.insiro.home.server.user.UserService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component


@Component
class AuthenticateProvider(
        private val userService: UserService,
        private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication {
        authentication ?: throw Exception("UnProcessable Authentication")//#TODO: impl Detailed Exception
        val user = userService.loadUserByUsername(authentication.name)
        val passwd = authentication.credentials.toString()
        if (!passwordEncoder.matches(passwd, user.password)) throw Exception("Password Failed") //#TODO: impl Detailed Exception
        return UsernamePasswordAuthenticationToken(user,passwd, user.authorities)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return authentication is UsernamePasswordAuthenticationToken
    }


}
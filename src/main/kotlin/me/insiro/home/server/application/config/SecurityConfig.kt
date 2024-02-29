package me.insiro.home.server.application.config

import me.insiro.home.server.user.UserService
import me.insiro.home.server.user.utils.AuthenticateProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(private val userService: UserService, private val authenticateProvider: AuthenticateProvider) {
    @Bean
    fun SecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http

                .securityContext { it.requireExplicitSave(true) }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
                .userDetailsService(userService).authenticationProvider(authenticateProvider)
                .csrf { it.disable() }
                .authorizeHttpRequests {
                    it.requestMatchers("/**").permitAll().anyRequest().authenticated()
                }

        return http.build()
    }
}
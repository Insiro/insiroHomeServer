package me.insiro.home.server.application.config.security

import me.insiro.home.server.user.UserService
import me.insiro.home.server.user.utils.AuthenticateProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfig(
        private val userService: UserService,
        private val authenticateProvider: AuthenticateProvider,
) {
    @Bean
    fun SecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http

                .securityContext { it.requireExplicitSave(true) }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
                .userDetailsService(userService).authenticationProvider(authenticateProvider)
                .csrf { it.disable() }
            .cors {
                it.configurationSource {
                    val config = CorsConfiguration()
                    val allowAll = listOf("*")
                    config.allowedHeaders = allowAll
                    config.allowedMethods = allowAll
                    config.allowCredentials = true
                    config.allowedOrigins = listOf("localhost:*")
                    config
                }
            }
                .authorizeHttpRequests {
                    it.requestMatchers("/**").permitAll().anyRequest().authenticated()
                }
                .exceptionHandling {
                    it.accessDeniedHandler(CustomAccessDeniedHandler())
                            .authenticationEntryPoint(CustomAuthenticationEntryPoint())
                }

        return http.build()
    }
}
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
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

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
            .addFilter(corsFilter())
            .authorizeHttpRequests {
                it.requestMatchers("/**").permitAll().anyRequest().authenticated()
            }
            .exceptionHandling {
                it.accessDeniedHandler(CustomAccessDeniedHandler())
                    .authenticationEntryPoint(CustomAuthenticationEntryPoint())
            }

        return http.build()
    }
    @Bean
    fun corsFilter(): CorsFilter {
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOrigins= listOf("https://insiro.me","https://test.insiro.me")
        config.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}
package me.insiro.home.server.application.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

class SecurityConfig {
    @Bean
    fun SecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers("/**").permitAll().anyRequest().authenticated()

        }.csrf { it.disable() }
        return http.build()
    }
}
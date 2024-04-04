package me.insiro.home.server.application.config

import org.springframework.stereotype.Component

@Component
data class ApplicationOptions(var env: String = "dev")

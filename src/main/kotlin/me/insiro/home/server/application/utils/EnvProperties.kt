package me.insiro.home.server.application.utils

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
@ConfigurationProperties(prefix = "")
@PropertySource("file:./data/env.yml")
annotation class EnvProperties(
    val prefix: String,
)
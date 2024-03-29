package me.insiro.home.server

import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
@ImportAutoConfiguration(ExposedAutoConfiguration::class)
class InsiroHomeServerApplication

fun main(args: Array<String>) {
    runApplication<InsiroHomeServerApplication>(*args)
}

package me.insiro.home.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InsiroHomeServerApplication

fun main(args: Array<String>) {
    runApplication<InsiroHomeServerApplication>(*args)
}

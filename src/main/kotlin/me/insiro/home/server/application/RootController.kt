package me.insiro.home.server.application

import me.insiro.home.server.application.config.ApplicationOptions
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RootController(val option: ApplicationOptions) {

    @GetMapping("status")
    fun applicationStatus ():ApplicationOptions{
    return option
    }
}
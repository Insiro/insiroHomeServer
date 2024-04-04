package me.insiro.home.server.application.config

import me.insiro.home.server.application.utils.EnvProperties

@EnvProperties("app")
data class ApplicationOptions(var mode:String="")
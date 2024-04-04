package me.insiro.home.server.application.dto

import me.insiro.home.server.application.config.ApplicationOptions

data class ApplicationStatus (val env:String){
    constructor(cliOptions: ApplicationOptions):this(cliOptions.env)
}
package me.insiro.home.server.application.config.intialize

import me.insiro.home.server.application.utils.EnvProperties

@EnvProperties("admin")
data class AdminAccountProperties(
    var name: String = "administrator",
    var password: String = "administrator",
    var email: String = ""
)
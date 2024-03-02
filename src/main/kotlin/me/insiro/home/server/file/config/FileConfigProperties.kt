package me.insiro.home.server.file.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("app.files")
data class FileConfigProperties(
    var storage: FileStorage? = FileStorage.None,
    val location: String = ""
) {
    enum class FileStorage {
        Local, None
    }
}
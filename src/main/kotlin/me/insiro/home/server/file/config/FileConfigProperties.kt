package me.insiro.home.server.file.config

import me.insiro.home.server.application.utils.EnvProperties


@EnvProperties("files")
data class FileConfigProperties(
    var storage: FileStorage? = FileStorage.None,
    var location: String = "./data/static"
) {
    enum class FileStorage {
        Local, None
    }
}

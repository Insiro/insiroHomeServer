package me.insiro.home.server.file.vo

sealed interface IFileItem: IFileCollection {
    val name: String
}
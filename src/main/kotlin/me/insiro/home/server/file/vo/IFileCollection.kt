package me.insiro.home.server.file.vo

sealed interface IFileCollection {
    val domain: String
    val collection: String
}
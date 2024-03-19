package me.insiro.home.server.application.domain

interface IModifyFileDTO {
    val title: String?
    val content:String?
    val deletedFileNames: List<String>?
}

package me.insiro.home.server.file.controller.fileServer

interface IFileServer<T : Any> {

    fun getItem(domain: String, collection: String, fName: String): T
}


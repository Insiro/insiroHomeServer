package me.insiro.home.server.file.controller.fileServer

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource

class StaticFileServer(private val location: String) : IFileServer<Resource> {
    override fun getItem(domain: String, collection: String, fName: String): Resource {
        return UrlResource("file:$location/${domain}/${collection}/${fName}")
    }
}
package me.insiro.home.server.file.controller

import me.insiro.home.server.file.controller.fileServer.IFileServer
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/static")
@Controller
class FileController(private val fileServer: IFileServer<*>) {
    @GetMapping("{domain}/{collection}/{fName}")
    fun getItem(
        @PathVariable collection: String,
        @PathVariable domain: String,
        @PathVariable fName: String
    ): ResponseEntity<*> {
        return ResponseEntity.ok().body(fileServer.getItem(domain, collection, fName))
    }
}
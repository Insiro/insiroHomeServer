package me.insiro.home.server.file.entity

import org.springframework.web.multipart.MultipartFile

sealed class File {


    data class FileHeaderVO(val name: String, val domain: String?)
    sealed class FileContentVO {
        data class TextFileVO(
                val name: String,
                val domain: String?,
                val content: String,
        )

        data class ImageFileVO(val name: String, val domain: String?, val multipartFile: MultipartFile)
    }
}
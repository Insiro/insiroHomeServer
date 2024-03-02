package me.insiro.home.server.file.vo

import java.io.File

object FileItemFactory {
    fun new(collection: IFileCollection, file: File): IFileItem {
        val ex = file.extension
        return when (ex) {
            "md", "txt" -> VOTextFileItem(collection, file.name)
            "jpg", "png", "jpeg" -> VOMediaFileItem(collection, file.name)
            else -> VOFileItem(collection, file.name)
        }
    }

    fun new(collection: IFileCollection, fileName: String): IFileItem {
        val file = File(fileName)
        return new(collection, file)
    }

}
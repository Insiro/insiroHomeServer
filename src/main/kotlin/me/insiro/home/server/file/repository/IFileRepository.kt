package me.insiro.home.server.file.repository

import me.insiro.home.server.file.vo.*
import org.springframework.web.multipart.MultipartFile


interface IFileRepository {
    val location: String
    //over domain
    fun getCollections(domain: String): List<IFileCollection>

    //over collection
    fun find(collection: IFileCollection): List<IFileItem>
    fun exist(fileVO: IFileItem): Boolean

    fun get(fileVO: IFileItem): IFileItem?


    fun get(fileVO: VOTextFileItem): VOTextFileItem?
    //by item
    fun load(fileVO: VOTextFileItem): VOTextFileItem?
    fun save(fileVO: IFileItem, data: Any): IFileItem {
        return when (fileVO) {
            is VOFileItem -> save(fileVO, data as ByteArray)
            is VOMediaFileItem -> {
                save(fileVO, data as MultipartFile)
            }

            is VOTextFileItem -> {
                save(fileVO, data as String)
            }
        }
    }
    fun save(fileVO: VOFileItem, data: ByteArray): VOFileItem

    fun save(textVO: VOTextFileItem): VOTextFileItem {
        return save(textVO, textVO.content ?: "")
    }

    fun save(contentVO: IFileItem, data: MultipartFile): IFileItem
    fun save(textVO: VOTextFileItem, data: String): VOTextFileItem
    fun delete(collection: VOFileCollection): Boolean
    fun delete(fileVO: IFileItem): Boolean
    fun append(textVO: VOTextFileItem, content: String): VOTextFileItem?
    fun addItem(textVO: IFileCollection, data: MultipartFile): IFileItem?
}
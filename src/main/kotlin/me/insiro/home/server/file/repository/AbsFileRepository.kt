package me.insiro.home.server.file.repository

import me.insiro.home.server.file.vo.*
import org.springframework.web.multipart.MultipartFile


abstract class AbsFileRepository(val locaton: String) {
    //over domain
    abstract fun getCollections(domain: String): List<IFileCollection>

    //over collection
    abstract fun find(collection: IFileCollection): List<IFileItem>

    abstract fun get(fileVO: IFileItem): IFileItem?

    //by item
    abstract fun load(fileVO: VOTextFileItem): VOTextFileItem?
    fun save(fileVO: IFileItem, data:Any): IFileItem {
        return when(fileVO){
            is VOFileItem -> TODO()
            is VOMediaFileItem -> {save(fileVO, data as MultipartFile)}
            is VOTextFileItem -> {save(fileVO , data as String)}
        }
    }
    fun save(textVO: VOTextFileItem):VOTextFileItem{return save(textVO, textVO.content?:"")}
    abstract fun save(contentVO: IFileItem, data: MultipartFile):IFileItem
    abstract fun save(textVO: VOTextFileItem, data: String):VOTextFileItem
    abstract fun delete(fileVO: IFileItem): Boolean
    abstract fun append(textVO: VOTextFileItem, content: String): VOTextFileItem
}
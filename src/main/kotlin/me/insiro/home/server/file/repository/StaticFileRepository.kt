package me.insiro.home.server.file.repository

import me.insiro.home.server.file.vo.IFileCollection
import me.insiro.home.server.file.vo.IFileItem
import me.insiro.home.server.file.vo.VOTextFileItem
import org.springframework.web.multipart.MultipartFile

class StaticFileRepository(location: String) : AbsFileRepository(location) {
    override fun getCollections(domain: String): List<IFileCollection> {
        TODO("Not yet implemented")
    }

    override fun find(collection: IFileCollection): List<IFileItem> {
        TODO("Not yet implemented")
    }

    override fun get(fileVO: IFileItem): IFileItem? {
        TODO("Not yet implemented")
    }

    override fun load(fileVO: VOTextFileItem): VOTextFileItem? {
        TODO("Not yet implemented")
    }

    override fun save(contentVO: IFileItem, data: MultipartFile): IFileItem {
        TODO("Not yet implemented")
    }

    override fun save(textVO: VOTextFileItem, data: String): VOTextFileItem {
        TODO("Not yet implemented")
    }

    override fun delete(fileVO: IFileItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun append(textVO: VOTextFileItem, content: String): VOTextFileItem {
        TODO("Not yet implemented")
    }
}
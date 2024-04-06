package me.insiro.home.server.file.repository

import me.insiro.home.server.file.exception.DirCreateException
import me.insiro.home.server.file.vo.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.AccessDeniedException
import java.nio.file.Path
import kotlin.io.path.*

class StaticFileRepository(override val location: String) : IFileRepository {
    private val path: Path = Path(location).toRealPath()
    private fun path(file: IFileCollection, mkdir: Boolean = false): Path {
        var filePath = path.resolve(file.domain).resolve(file.collection)
        if (mkdir && !filePath.exists())
            filePath.createDirectories()
        if (file is IFileItem)
            filePath = filePath.resolve(file.name)
        return filePath
    }

    init {
        try {
            if (path.notExists())
                path.createDirectories()
            else if (path.isDirectory().not())
                throw AccessDeniedException(path.toString())
        } catch (e: AccessDeniedException) {
            throw DirCreateException(path)
        }

    }

    //over domain
    override fun getCollections(domain: String): List<IFileCollection> {
        val domainDir = path.resolve(domain)
        val collections = arrayListOf<IFileCollection>()
        for (collectionDir in domainDir.listDirectoryEntries()) {
            collections.add(VOFileCollection(domain, collectionDir.name))
        }
        return collections
    }

    //over collection
    override fun find(collection: IFileCollection): List<IFileItem> {
        val collectionDir = path(collection)
        val files = arrayListOf<IFileItem>()
        for (item in collectionDir.listDirectoryEntries()) {
            files.add(FileItemFactory.new(collection, item.toFile()))
        }
        return files
    }

    //by item
    override fun exist(fileVO: IFileItem): Boolean {
        return path(fileVO).toFile().isFile.not()
    }

    override fun get(fileVO: IFileItem): IFileItem? {
        val file = path(fileVO).toFile()
        if (file.isFile.not()) return null
        return FileItemFactory.new(fileVO, file)
    }

    override fun get(fileVO: VOTextFileItem): VOTextFileItem? {
        val file = path(fileVO).toFile()
        return if (file.isFile.not()) null else fileVO
    }

    override fun load(fileVO: VOTextFileItem): VOTextFileItem? {
        val filePath = path(fileVO)
        val file = filePath.toFile()
        if (file.isFile.not())
            return null
        return fileVO.copy(content = file.readText())
    }

    override fun save(fileVO: VOFileItem, data: ByteArray): VOFileItem {
        val filePath = path(fileVO)
        val file = filePath.toFile()
        if (file.isFile)
            file.delete()
        file.createNewFile()
        file.writeBytes(data)
        return fileVO
    }

    override fun save(textVO: VOTextFileItem, data: String): VOTextFileItem {
        path(textVO, mkdir = true)
        val file = path(textVO).toFile()
        if (!file.exists())
            file.createNewFile()
        file.writeText(data)
        return textVO.copy(content = data)
    }

    override fun save(contentVO: IFileItem, data: MultipartFile): IFileItem {
        path(contentVO, mkdir = true)
        val file = path(contentVO).toFile()
        data.transferTo(file)
        return contentVO
    }

    override fun append(textVO: VOTextFileItem, content: String): VOTextFileItem? {
        val file = path(textVO).toFile()
        if (file.isFile.not())
            return null
        file.appendText(content)
        return textVO.copy(content = textVO.content + content)
    }

    override fun delete(collection: VOFileCollection): Boolean {
        val filePath = path(collection)
        if (!filePath.isDirectory())
            return false
        return filePath.toFile().deleteRecursively()
    }

    override fun delete(fileVO: IFileItem): Boolean {
        if (fileVO.name == "index.md")
            return delete(VOFileCollection(fileVO))
        val filePath = path(fileVO)
        return filePath.toFile().delete()
    }

    override fun addItem(textVO: IFileCollection, data: MultipartFile): IFileItem? {
        val filePath = path(textVO)
        if (filePath.parent.notExists())
            return null
        val fineName = data.originalFilename ?: return null
        val fileVO = FileItemFactory.new(textVO, fineName)

        data.transferTo(path(fileVO).toFile())
        return fileVO
    }

}

package me.insiro.home.server.file.service

import me.insiro.home.server.application.domain.dto.IModifyFileDTO
import me.insiro.home.server.application.domain.entity.IEntityVO
import me.insiro.home.server.file.repository.IFileRepository
import me.insiro.home.server.file.vo.IFileItem
import me.insiro.home.server.file.vo.VOFileCollection
import me.insiro.home.server.file.vo.VOFileItem
import me.insiro.home.server.file.vo.VOTextFileItem
import org.springframework.web.multipart.MultipartFile

abstract class AbsFileService<VO : IEntityVO<*>>(
    protected val domain: String,
    protected val repository: IFileRepository
) {

    protected abstract fun collectionName(vo: VO): String

    fun create(
        vo: VO,
        content: String,
        files: List<MultipartFile>?
    ): List<IFileItem> {
        assert(vo.id != null)
        val index = repository.save(VOTextFileItem(domain, collectionName(vo), "index.md", content))
        val items = arrayListOf<IFileItem>(index)
        files?.forEach { file -> repository.addItem(index, file)?.apply { items.add(this) } }
        return items
    }


    protected fun get(collection: String, load: Boolean = false): VOTextFileItem? {
        val textFileItem = VOTextFileItem(domain, collection, "index.md")
        return if (load) repository.load(textFileItem) else repository.get(textFileItem)
    }

    fun iconPath(vo: VO): String? {
        return iconPath(collectionName(vo))
    }

    private fun iconPath(collection: String): String? {
        val iconFile = VOFileItem(domain, collection, "icon.png")
        if (repository.exist(iconFile))
            return "static/${domain}/${collection}/icon.png"
        val preview = iconFile.copy(name= "preview.png")
        if (repository.exist(preview))
            return "static/${domain}/${collection}/preview.png"
        return null
    }

    protected fun get(collection: String, item: String): IFileItem? {
        return repository.get(VOFileItem(domain, collection, item))
    }

    fun get(vo: VO, load: Boolean): VOTextFileItem? {
        assert(vo.id != null)
        return get(collectionName(vo), load)
    }

    fun get(vo: VO, item: String): IFileItem? {
        assert(vo.id != null)
        return get(collectionName(vo), item)
    }


    fun delete(vo: VO): Boolean {
        return repository.delete(VOFileCollection(domain, collectionName(vo)))
    }


    protected fun delete(collection: String, item: String): Boolean {
        return repository.delete(VOFileItem(domain, collection, item))
    }

    protected fun find(collection: String): List<IFileItem> {
        return repository.find(VOFileCollection(domain, collection))
    }

    fun find(vo: VO): List<IFileItem> {
        assert(vo.id != null)
        return find(collectionName(vo))
    }

    protected fun update(item: VOTextFileItem, delete: List<String>?, create: List<MultipartFile>?): String? {
        item.content?.let{ repository.save(item).content}
        delete?.forEach { repository.delete(VOFileItem(item, it)) }
        create?.forEach { repository.addItem(item, it) }
        return item.content
    }


    fun update(vo: VO, modifyDTO: IModifyFileDTO, files: List<MultipartFile>?): String? {
        return update(VOTextFileItem(domain, collectionName(vo), "index.md"), modifyDTO.deletedFileNames, files)
    }


}
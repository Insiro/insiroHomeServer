import me.insiro.home.server.application.domain.IEntityVO
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

    abstract fun collectionName(vo: VO): String
    abstract fun create(vo: VO, content: String, files: List<MultipartFile>? = null): List<IFileItem>

    protected fun create(
        collection: String,
        content: String,
        files: List<MultipartFile>? = null
    ): List<IFileItem> {
        val index = repository.save(VOTextFileItem(domain, collection, "index.md", content))
        val items = arrayListOf<IFileItem>(index)
        files?.forEach { file -> repository.addItem(index, file)?.apply { items.add(this) } }
        return items
    }

    protected fun get(collection: String, load: Boolean = false): VOTextFileItem? {
        val textFileItem = VOTextFileItem(domain, collection, "index.md")
        return if (load) repository.load(textFileItem) else repository.get(textFileItem)
    }

    protected fun get(collection: String, item: String): IFileItem? {
        return repository.get(VOFileItem(domain, collection, item))
    }

    abstract fun get(vo: VO, load: Boolean = false): VOTextFileItem?

    abstract fun get(vo: VO, item: String): IFileItem?

    abstract fun delete(vo: VO): Boolean
    protected fun delete(collection: String, item: String): Boolean {
        return repository.delete(VOFileItem(domain, collection, item))
    }

    protected fun find(collection: String): List<IFileItem> {
        return repository.find(VOFileCollection(domain, collection))
    }

    abstract fun find(vo: VO): List<IFileItem>

    protected fun update(item: VOTextFileItem, delete: List<String>?, create: List<MultipartFile>?) {
        repository.save(item)
        delete?.forEach { repository.delete(VOFileItem(item, it)) }
        create?.forEach { repository.addItem(item, it) }
    }
}
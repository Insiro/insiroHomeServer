package me.insiro.home.server.file.service

import AbsFileService
import me.insiro.home.server.file.repository.IFileRepository
import me.insiro.home.server.file.vo.IFileItem
import me.insiro.home.server.file.vo.VOFileCollection
import me.insiro.home.server.file.vo.VOTextFileItem
import me.insiro.home.server.post.dto.post.UpdatePostDTO
import me.insiro.home.server.post.entity.Post
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class PostFileService
    (
    fileRepository: IFileRepository
) : AbsFileService<Post>("post", fileRepository) {
    override fun collectionName(post: Post): String {
        assert(post.id != null)
        return "${post.id!!.value}_${post.title}"
    }

    override fun create(
        post: Post,
        content: String,
        files: List<MultipartFile>?
    ): List<IFileItem> {
        assert(post.id != null)
        return create(collectionName(post), content, files)
    }

    override fun get(post: Post, load: Boolean): VOTextFileItem? {
        assert(post.id != null)
        return get(collectionName(post), load)
    }

    override fun get(post: Post, item: String): IFileItem? {
        assert(post.id != null)
        return get(collectionName(post), item)
    }

    override fun delete(post: Post): Boolean {
        return repository.delete(VOFileCollection(domain, collectionName(post)))
    }

    override fun find(post: Post): List<IFileItem> {
        assert(post.id != null)
        return find(collectionName(post))
    }

    fun update(post: Post, updatePostDTO: UpdatePostDTO, files: List<MultipartFile>?) {
        update(VOTextFileItem(domain, collectionName(post), "index.md"), updatePostDTO.deletedItems, files)
    }

}
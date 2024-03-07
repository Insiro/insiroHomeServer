package me.insiro.home.server.post.service

import me.insiro.home.server.post.dto.post.NewPostDTO
import me.insiro.home.server.post.dto.post.UpdatePostDTO
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.post.exception.post.PostModifyForbiddenException
import me.insiro.home.server.post.repository.PostRepository
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import org.springframework.stereotype.Service

@Service
class PostService(private val postRepository: PostRepository) {
    @Throws(PostModifyForbiddenException::class)
    private fun validateModifyPermission(post:Post, user:User){
        val authorId = when(post){
            is Post.Joined ->post.author.id
            is Post.Raw -> post.authorId
        }
        if (authorId != user.id || UserRole.ROLE_ADMIN.isGranted(user.permission))
            throw PostModifyForbiddenException(post.id!!, user.id!!)
    }
    fun createPost(createDTO: NewPostDTO, user: User, categoryId: Category.Id? = null): Post.Raw {
        val post = Post.Raw(createDTO.title, createDTO.status, user.id!!, categoryId)
        //TODO: save file content Using FileService
        return postRepository.new(post)
    }

    fun updatePost(id: Post.Id, updateDTO: UpdatePostDTO, categoryId: Category.Id?, user: User): Post.Raw? {
        val post = postRepository.findById(id) ?: return null
        validateModifyPermission(post, user)

        val updated =
            postRepository.update(id, categoryId = categoryId, title = updateDTO.title, status = updateDTO.status)
        //TODO: update file content Using FileService
        return updated
    }

    fun deletePost(id: Post.Id, user: User): Boolean {
        val post = postRepository.findById(id) ?: return false
        validateModifyPermission(post, user)
        return postRepository.delete(id)
        //TODO: delete file content Using FileService
    }

    fun findJoinedPost(id:Post.Id):Post.Joined?{
        return postRepository.findByIdJoining(id)
    }

    fun findPost(id: Post.Id): Post.Raw? {
        return postRepository.findById(id)
    }

    fun findPosts(limit: Int = 0, offset: Long? = null): List<Post.Raw> {
        return postRepository.find(limit, offset)
    }

    fun findJoinedPosts(limit: Int = 0, offset: Long? = null): List<Post.Joined> {
        return postRepository.findJoining(limit = limit, offset = offset)
    }

    fun findPostsByCategory(id: Category.Id): List<Post.Joined> {
        return postRepository.findJoining(categoryId = id)
    }

    fun changeCategoryOfPosts(id: Category.Id, newId: Category.Id?): Int {
        return postRepository.updateCategory(id, newId)
    }

}


package me.insiro.home.server.post.service

import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.post.dto.post.NewPostDTO
import me.insiro.home.server.post.dto.post.UpdatePostDTO
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.post.exception.post.PostDuplicatedException
import me.insiro.home.server.post.exception.post.PostModifyForbiddenException
import me.insiro.home.server.post.exception.post.PostNotFoundException
import me.insiro.home.server.post.repository.PostRepository
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import org.springframework.stereotype.Service

@Service
class PostService(private val postRepository: PostRepository) {
    private fun validateModifyPermission(post: Post, user: User): Result<Unit> {
        val authorId = when (post) {
            is Post.Joined -> post.author.id
            is Post.Raw -> post.authorId
        }
        if (authorId != user.id && !UserRole.ROLE_ADMIN.isGranted(user))
            return Result.failure(PostModifyForbiddenException(post.id!!, user.id!!))
        return Result.success(Unit)
    }

    fun createPost(createDTO: NewPostDTO, user: User, categoryId: Category.Id? = null): Result<Post.Raw> {
        val post = Post.Raw(createDTO.title, createDTO.status ?: Status.PUBLISHED, user.id!!, categoryId)
        return when (val id = createDTO.id) {
            null -> postRepository.new(post)
            else -> postRepository.new(post, id)
        }?.let { Result.success(it) }
            ?: return Result.failure(PostDuplicatedException(createDTO.id!!))

    }

    fun updatePost(id: Post.Id, updateDTO: UpdatePostDTO, categoryId: Category.Id?, user: User): Result<Post.Raw> {
        val post = findPost(id).getOrElse { return Result.failure(it) }
        validateModifyPermission(post, user).getOrElse { return Result.failure(it) }

        val updated =
            postRepository.update(id, categoryId = categoryId, title = updateDTO.title, status = updateDTO.status)
        return Result.success(updated)
    }

    fun deletePost(id: Post.Id, user: User): Result<Boolean> {
        val post = findPost(id).getOrElse { return Result.failure(it) }
        validateModifyPermission(post, user).getOrElse { return Result.failure(it) }
        val result = postRepository.delete(post)
        return Result.success(result)
    }

    fun findJoinedPost(id: Post.Id): Result<Post.Joined> {
        return postRepository.findByIdJoining(id)
            ?.let { Result.success(it) }
            ?: Result.failure(PostNotFoundException(id))
    }

    fun findPost(id: Post.Id): Result<Post.Raw> {
        return postRepository.findById(id)?.let { Result.success(it) } ?: Result.failure(PostNotFoundException(id))
    }

    fun findPosts(
        id: Category.Id? = null,
        status: List<Status>? = null,
        offsetLimit: OffsetLimit? = null
    ): List<Post.Raw> {
        return postRepository.find(id, status, offsetLimit)
    }

    fun findJoinedPosts(
        id: Category.Id? = null,
        status: List<Status>? = null,
        offsetLimit: OffsetLimit? = null
    ): List<Post.Joined> {
        return postRepository.findJoining(categoryId = id, status = status, offsetLimit = offsetLimit)
    }

    fun changeCategoryOfPosts(id: Category.Id, newId: Category.Id?): Int {
        return postRepository.updateCategory(id, newId)
    }

}


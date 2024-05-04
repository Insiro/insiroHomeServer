package me.insiro.home.server.post.service

import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.exception.UnAuthorizedException
import me.insiro.home.server.post.dto.comment.ModifierDTO
import me.insiro.home.server.post.dto.comment.ModifyCommentDTO
import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.CommentUserInfo
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.post.exception.comment.CommentModifyForbiddenException
import me.insiro.home.server.post.exception.comment.CommentNotFoundException
import me.insiro.home.server.post.repository.CommentRepository
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.utils.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    private fun validateModify(comment: Comment, modifyDTO: ModifierDTO, user: User? = null): Result<Unit> {
        assert(comment.id != null)
        when (val author = comment.author) {
            is CommentUserInfo.Anonymous -> {
                val info = modifyDTO as ModifierDTO.Anonymous
                if (author.name != info.name && passwordEncoder.matches(info.password, author.pwd))
                    return Result.failure(CommentModifyForbiddenException(comment.id!!, author.name))
            }

            is CommentUserInfo.UserInfo -> {
                if (author.id != user?.id && !UserRole.ROLE_ADMIN.isGranted(user))
                    return Result.failure(CommentModifyForbiddenException(comment.id!!, user?.id))
            }
        }
        return Result.success(Unit)
    }

    private fun createUserInfo(commentDTO: ModifierDTO, user: User?): CommentUserInfo {
        return when (commentDTO) {
            is ModifierDTO.Anonymous -> CommentUserInfo.Anonymous(
                commentDTO.name,
                passwordEncoder.encode(commentDTO.password)
            )

            is ModifierDTO.Signed -> user?.let { CommentUserInfo.UserInfo(user) } ?: throw UnAuthorizedException()
        }
    }

    fun updateComment(id: Comment.Id, updateDto: ModifyCommentDTO, user: User? = null): Result<Comment> {
        assert(user != null || updateDto.user is ModifierDTO.Anonymous)
        // check not found
        val comment = getComment(id).getOrElse { return Result.failure(it) }
        validateModify(comment, updateDto.user, user).getOrElse { return Result.failure(it) }

        val updated = commentRepository.update(comment.copy(content = updateDto.content))
        return Result.success(updated)
    }

    fun findComments(id: Post.Id? = null, offsetLimit: OffsetLimit? = null, parent: Comment.Id? = null): List<Comment> {
        return commentRepository.find(postId = id, offsetLimit = offsetLimit, parentId = parent)
    }

    fun deleteComment(id: Comment.Id, modifyDTO: ModifierDTO, user: User? = null): Result<Boolean> {
        assert(user != null || modifyDTO is ModifierDTO.Anonymous)
        // check not found
        val comment = getComment(id).getOrElse { return Result.failure(it) }
        validateModify(comment, modifyDTO, user).getOrElse { return Result.failure(it) }
        return Result.success(commentRepository.delete(comment))
    }

    fun getComment(id: Comment.Id): Result<Comment> {
        return commentRepository.findById(id)
            ?.let { Result.success(it) }
            ?: return Result.failure(CommentNotFoundException(id))
    }


    fun addComment(postId: Post.Id, commentDTO: ModifyCommentDTO, user: User? = null): Comment {
        assert(user != null || commentDTO.user is ModifierDTO.Anonymous)
        val authorInfo = createUserInfo(commentDTO.user, user)
        val comment = Comment(commentDTO.content, postId, null, authorInfo)
        return commentRepository.new(comment)
    }

    fun appendComment(parent: Comment, commentDTO: ModifyCommentDTO, user: User? = null): Comment {
        assert(user != null || commentDTO.user is ModifierDTO.Anonymous)
        val authorInfo = createUserInfo(commentDTO.user, user)
        val comment = Comment(commentDTO.content, parent.postId, parent.id, authorInfo)
        return commentRepository.new(comment)
    }

    fun deleteCommentByPostId(postId: Post.Id): Int {
        return commentRepository.delete(postId = postId)
    }
}
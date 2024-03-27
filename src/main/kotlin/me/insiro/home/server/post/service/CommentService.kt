package me.insiro.home.server.post.service

import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.exception.UnAuthorizedException
import me.insiro.home.server.post.dto.comment.ModifierDTO
import me.insiro.home.server.post.dto.comment.ModifyCommentDTO
import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.CommentUserInfo
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.post.exception.comment.CommentModifyForbiddenException
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
    @Throws(CommentModifyForbiddenException::class)
    private fun validateModify(comment: Comment, modifyDTO: ModifierDTO, user: User? = null) {
        assert(comment.id != null)
        when (val author = comment.author) {
            is CommentUserInfo.Anonymous -> {
                val info = modifyDTO as ModifierDTO.IAnonymous
                if (author.name != info.name && passwordEncoder.matches(info.password, author.pwd))
                    throw CommentModifyForbiddenException(comment.id!!, author.name)
            }

            is CommentUserInfo.UserInfo -> {
                if (author.id != user?.id && !UserRole.ROLE_ADMIN.isGranted(user))
                    throw CommentModifyForbiddenException(comment.id!!, user?.id)
            }
        }
    }

    private fun createUserInfo(commentDTO: ModifyCommentDTO, user: User?): CommentUserInfo {
        return when (commentDTO) {
            is ModifyCommentDTO.Anonymous -> CommentUserInfo.Anonymous(
                commentDTO.name,
                passwordEncoder.encode(commentDTO.password)
            )

            is ModifyCommentDTO.Signed -> user?.let { CommentUserInfo.UserInfo(user) }?:throw UnAuthorizedException()
        }
    }

    fun updateComment(id: Comment.Id, updateDto: ModifyCommentDTO, user: User? = null): Comment? {
        assert(user != null || updateDto is ModifyCommentDTO.Anonymous)
        // check not found
        val comment = commentRepository.findById(id) ?: return null
        validateModify(comment, updateDto, user)

        val updated = comment.copy(content = updateDto.content)
        return commentRepository.update(updated)
    }

    fun findComments(id: Post.Id?=null, offsetLimit: OffsetLimit?=null, parent:Comment.Id?=null): List<Comment> {
        return commentRepository.find(postId = id, offsetLimit = offsetLimit, parentId = parent)
    }

    fun deleteComment(id: Comment.Id, modifyDTO: ModifierDTO, user: User? = null): Boolean {
        assert(user != null || modifyDTO is ModifierDTO.Anonymous)
        val comment = commentRepository.findById(id) ?: return false
        validateModify(comment, modifyDTO, user)
        return (commentRepository.delete(comment))
    }

    fun getComment(id: Comment.Id): Comment? {
        return commentRepository.findById(id)
    }


    fun addComment(postId: Post.Id, commentDTO: ModifyCommentDTO, user: User? = null): Comment {
        assert(user != null || commentDTO is ModifyCommentDTO.Anonymous)
        val authorInfo = createUserInfo(commentDTO, user)
        val comment = Comment(commentDTO.content, postId, null, authorInfo)
        return commentRepository.new(comment)
    }

    fun appendComment(parent: Comment, commentDTO: ModifyCommentDTO, user: User? = null): Comment {
        assert(user != null || commentDTO is ModifyCommentDTO.Anonymous)
        val authorInfo = createUserInfo(commentDTO, user)
        val comment = Comment(commentDTO.content, parent.postId, parent.id, authorInfo)
        return commentRepository.new(comment)
    }

    fun deleteCommentByPostId(postId: Post.Id): Int {
        return commentRepository.delete(postId = postId)
    }
}
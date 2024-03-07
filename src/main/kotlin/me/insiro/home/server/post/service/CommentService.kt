package me.insiro.home.server.post.service

import me.insiro.home.server.post.dto.comment.ModifyCommentDTO
import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.CommentUserInfo
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.post.exception.comment.CommentModifyForbiddenException
import me.insiro.home.server.post.repository.CommentRepository
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.utils.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun findComments(id: Post.Id): List<Comment> {
        return commentRepository.find(postId = id)
    }

    fun deleteComment(id: Comment.Id): Boolean {
        return (commentRepository.delete(id))
    }

    fun getComment(id: Comment.Id): Comment? {
        return commentRepository.findById(id)
    }


    fun updateComment(id: Comment.Id, updateDto: ModifyCommentDTO, user: User? = null): Comment? {
        assert(user != null || updateDto is ModifyCommentDTO.Anonymous)
        // check not found
        val comment = commentRepository.findById(id) ?: return null
        //region permission check
        when (val author = comment.author) {
            is CommentUserInfo.Anonymous -> {
                val info = updateDto as ModifyCommentDTO.Anonymous
                if (author.name != info.name && passwordEncoder.matches(info.password, author.pwd))
                    throw CommentModifyForbiddenException(id, author.name)
            }

            is CommentUserInfo.UserInfo -> {
                if (author.id != user!!.id)
                    throw CommentModifyForbiddenException(id, user.id!!)
            }
        }
        //endregion

        val updated = comment.copy(content = updateDto.content)
        return commentRepository.update(updated)
    }

    fun createUserInfo(commentDTO: ModifyCommentDTO, user: User?): CommentUserInfo {
        return when (commentDTO) {
            is ModifyCommentDTO.Anonymous -> CommentUserInfo.Anonymous(
                commentDTO.name,
                passwordEncoder.encode(commentDTO.password)
            )
            is ModifyCommentDTO.Signed -> CommentUserInfo.UserInfo(user!!)
        }
    }


    fun addComment(postId: Post.Id, commentDTO: ModifyCommentDTO, user: User? = null): Comment {
        assert(user != null || commentDTO is ModifyCommentDTO.Anonymous)
        val authorInfo = createUserInfo(commentDTO, user)
        val comment = Comment(commentDTO.content, postId, null, authorInfo)
        return commentRepository.new(comment)
    }

    fun appendComment(parent: Comment, commentDTO: ModifyCommentDTO, user: User? = null): Comment? {
        assert(user != null || commentDTO is ModifyCommentDTO.Anonymous)
        val authorInfo = createUserInfo(commentDTO, user)
        val comment = Comment(commentDTO.content, parent.postId, parent.id, authorInfo)
        return commentRepository.new(comment)
    }

    fun deleteCommentByPostId(postId: Post.Id): Int {
        return commentRepository.delete(postId = postId)
    }
}
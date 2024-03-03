package me.insiro.home.server.post.service

import me.insiro.home.server.post.dto.comment.ModifyCommentDTO
import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.user.entity.User
import org.springframework.stereotype.Service

@Service
class CommentService {
    fun findComments(id: Post.Id): List<Comment> {
        TODO("Not Yet Implemented")
    }

    fun deleteComment(id: Post.Id): Int {
        TODO("Not Yet Implemented")

    }
    fun addComment(id: Post.Id, anonymousDTO: ModifyCommentDTO): Comment {
        TODO("Not Yet Implemented")
    }
    fun addComment(id: Post.Id, anonymousDTO: ModifyCommentDTO, userId: User.Id): Comment {
        TODO("Not Yet Implemented")
    }
}
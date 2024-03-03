package me.insiro.home.server.post.service

import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.Post
import org.springframework.stereotype.Service

@Service
class CommentService {
    fun findComments(id: Post.Id): List<Comment> {
        TODO("Not Yet Implemented")
    }

    fun deleteComment(id: Post.Id): Int {
        TODO("Not Yet Implemented")

    }
}
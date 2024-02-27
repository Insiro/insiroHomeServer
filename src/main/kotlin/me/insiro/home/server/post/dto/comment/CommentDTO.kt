package me.insiro.home.server.post.dto.comment

data class CommentDTO(
        val id:Long,
        val content: String,
        val userName: String,
        val userId: String?,
)

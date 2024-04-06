package me.insiro.home.server.post.dto.comment

data class ModifyCommentDTO(
    val content: String,
    val user: ModifierDTO
)
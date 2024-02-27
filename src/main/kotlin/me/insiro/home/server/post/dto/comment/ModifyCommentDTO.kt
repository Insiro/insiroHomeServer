package me.insiro.home.server.post.dto.comment

data class ModifyCommentDTO(val content: String)

data class ModifyAnomalousCommentDTO(
        val content: String,
        val name: String,
        val password: String,
)

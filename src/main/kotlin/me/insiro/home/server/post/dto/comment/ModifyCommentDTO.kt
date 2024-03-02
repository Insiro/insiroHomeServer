package me.insiro.home.server.post.dto.comment

sealed interface ModifyCommentDTO {
    val content: String

    data class Signed(override val content: String) : ModifyCommentDTO

    data class Anonymous(
        override val content: String,
        val name: String,
        val password: String,
    ) : ModifyCommentDTO
}
package me.insiro.home.server.post.dto.comment


sealed interface ModifyCommentDTO : ModifierDTO {
    val content: String

    data class Signed(override val content: String) : ModifyCommentDTO, ModifierDTO.ISigned

    data class Anonymous(
        override val content: String,
        override val name: String,
        override val password: String,
    ) : ModifyCommentDTO, ModifierDTO.IAnonymous
}
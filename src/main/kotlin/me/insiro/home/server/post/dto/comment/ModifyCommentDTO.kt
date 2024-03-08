package me.insiro.home.server.post.dto.comment

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ModifyCommentDTO.Signed::class, name = "signed"),
    JsonSubTypes.Type(value = ModifyCommentDTO.Anonymous::class, name = "anonymous")
)
sealed interface ModifyCommentDTO : ModifierDTO {
    val content: String

    data class Signed(override val content: String) : ModifyCommentDTO, ModifierDTO.ISigned

    data class Anonymous(
        override val content: String,
        override val name: String,
        override val password: String,
    ) : ModifyCommentDTO, ModifierDTO.IAnonymous
}
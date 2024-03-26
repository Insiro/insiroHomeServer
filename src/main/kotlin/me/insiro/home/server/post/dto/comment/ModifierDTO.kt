package me.insiro.home.server.post.dto.comment

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ModifierDTO.Signed::class, name = "signed"),
    JsonSubTypes.Type(value = ModifierDTO.Anonymous::class, name = "anonymous")
)
sealed interface ModifierDTO {
    interface ISigned : ModifierDTO
    interface IAnonymous : ModifierDTO {
        val name: String
        val password: String
    }

    open class Signed : ISigned
    data class Anonymous(override val name: String, override val password: String) : IAnonymous
}
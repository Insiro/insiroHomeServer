package me.insiro.home.server.post.dto.comment

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ModifierDTO.Signed::class, name = "signed"),
    JsonSubTypes.Type(value = ModifierDTO.Anonymous::class, name = "anonymous")
)
sealed interface ModifierDTO {
    open class Signed : ModifierDTO
    data class Anonymous( val name: String,  val password: String) : ModifierDTO
}
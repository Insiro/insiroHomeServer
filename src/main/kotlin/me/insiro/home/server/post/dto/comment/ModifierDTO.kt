package me.insiro.home.server.post.dto.comment

sealed interface ModifierDTO {
    interface ISigned : ModifierDTO
    interface IAnonymous : ModifierDTO {
        val name: String
        val password: String
    }

    open class Signed : ISigned
    data class Anonymous(override val name: String, override val password: String) : IAnonymous
}
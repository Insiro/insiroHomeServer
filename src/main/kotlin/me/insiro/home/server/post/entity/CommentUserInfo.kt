package me.insiro.home.server.post.entity
import kotlinx.serialization.Serializable
import me.insiro.home.server.user.entity.User

@Serializable
sealed interface CommentUserInfo {
    @Serializable
    data class UserInfo(val id: User.Id, val name:String):CommentUserInfo{
        constructor(user:User):this(user.id!!, user.name)
    }

    @Serializable
    data class Anonymous(val name: String, val pwd: String? = null):CommentUserInfo

}
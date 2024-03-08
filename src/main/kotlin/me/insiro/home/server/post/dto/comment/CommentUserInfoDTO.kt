package me.insiro.home.server.post.dto.comment

import kotlinx.serialization.Serializable
import me.insiro.home.server.post.entity.CommentUserInfo
import me.insiro.home.server.user.entity.User

@Serializable
sealed interface CommentUserInfoDTO {
    val type: String

    @Serializable
    data class UserInfo(val id: User.Id, val name: String) : CommentUserInfoDTO {

        override val type: String = "signed"
    }

    @Serializable
    data class Anonymous(val name: String) : CommentUserInfoDTO {
        override val type: String = "anonymous"
    }

    companion object {
        fun new(userInfo: CommentUserInfo): CommentUserInfoDTO {
            return when (userInfo) {
                is CommentUserInfo.Anonymous -> Anonymous(userInfo.name)
                is CommentUserInfo.UserInfo -> UserInfo(userInfo.id, userInfo.name)
            }
        }
    }

}
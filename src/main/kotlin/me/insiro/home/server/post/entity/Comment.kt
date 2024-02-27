package me.insiro.home.server.post.entity

import kotlinx.serialization.Serializable
import me.insiro.home.server.application.domain.EntityVO

@Serializable
sealed class CommentUserInfo{
    @Serializable
    data class UserInfo(val userId:String)
    @Serializable
    data class AndrogynousInfo(val name: String, val pwd: String? = null)
}
data class Comment(
        var content:String,
        var postId: Long,
        var parentId: Long?,
        var author:CommentUserInfo
):EntityVO<Long>()

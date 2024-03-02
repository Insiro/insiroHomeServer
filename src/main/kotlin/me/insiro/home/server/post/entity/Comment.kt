package me.insiro.home.server.post.entity

import kotlinx.serialization.Serializable
import me.insiro.home.server.application.domain.BaseEntityVO
import org.jetbrains.exposed.dao.id.EntityID

@Serializable
sealed class CommentUserInfo {
    @Serializable
    data class UserInfo(val userId: String)

    @Serializable
    data class AndrogynousInfo(val name: String, val pwd: String? = null)
}

data class Comment(
    var content: String,
    var postId: Long,
    var parentId: Long?,
    var author: CommentUserInfo,
    override val id: Id?,
) : BaseEntityVO() {
    @JvmInline
    value class Id(override val value: Long) : BaseEntityVO.Id {
        constructor(entityID: EntityID<Long>) : this(entityID.value)
    }
}

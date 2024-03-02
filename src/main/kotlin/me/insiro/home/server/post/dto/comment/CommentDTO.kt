package me.insiro.home.server.post.dto.comment

import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.CommentUserInfo
import me.insiro.home.server.user.dto.UserDTO


sealed interface CommentDTO {
    val id: Comment.Id
    val content: String
    data class Content(
        override val id: Comment.Id,
        override val content: String,
    ) : CommentDTO{
        constructor(dto:CommentDTO):this(dto.id, dto.content)
    }

    data class Signed(
        override val id: Comment.Id,
        override val content: String,
        val user: UserDTO,
    ) : CommentDTO{
        constructor(dto:CommentDTO, user:UserDTO):this(dto.id, dto.content, user)
    }

    data class Anonymous(
        override val id: Comment.Id,
        override val content: String,
        val guest: Guest,
    ) : CommentDTO{
        constructor(dto:CommentDTO, guest:Guest):this(dto.id, dto.content, guest)
    }
    @JvmInline
    value class Guest(val name:String){
        constructor(user:CommentUserInfo.Anonymous):this(user.name)
    }
}
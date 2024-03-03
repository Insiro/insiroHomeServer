package me.insiro.home.server.post.dto.comment

import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.CommentUserInfo


data class CommentDTO(
    val id: Long,
    val content: String,
    val user: CommentUserInfo,
){
    constructor(comment:Comment):this(comment.id!!.value, comment.content, comment.author)
}

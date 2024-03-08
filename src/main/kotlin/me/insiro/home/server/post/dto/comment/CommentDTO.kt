package me.insiro.home.server.post.dto.comment

import me.insiro.home.server.application.domain.IResponseDTO
import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.entity.CommentUserInfo
import java.time.LocalDateTime


data class CommentDTO(
    override val id: Long,
    val content: String,
    val user: CommentUserInfo,
    override val createdAt: LocalDateTime,
    val children: List<CommentDTO>? = null
) : IResponseDTO<Long> {
    constructor(comment: Comment) : this(comment.id!!.value, comment.content, comment.author, comment.createdAt!!)
}

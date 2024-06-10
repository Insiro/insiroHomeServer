package me.insiro.home.server.post.dto.comment

import me.insiro.home.server.application.domain.dto.IResponseDTO
import me.insiro.home.server.application.domain.entity.ICreatedAt
import me.insiro.home.server.post.entity.Comment
import java.time.LocalDateTime


data class CommentDTO(
    override val id: Long,
    val content: String,
    val parentId: Long?,
    val user: CommentUserInfoDTO,
    override val createdAt: LocalDateTime,
    val children: List<CommentDTO>? = null
) : IResponseDTO<Long>,ICreatedAt {
    constructor(comment: Comment) : this(
        comment.id!!.value,
        comment.content,
        comment.parentId?.value,
        CommentUserInfoDTO.new(comment.author),
        comment.createdAt!!
    )
}

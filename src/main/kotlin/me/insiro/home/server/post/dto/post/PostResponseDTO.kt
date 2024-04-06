package me.insiro.home.server.post.dto.post

import me.insiro.home.server.application.domain.dto.IFileIcon
import me.insiro.home.server.application.domain.dto.IResponseDTO
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.file.vo.IFileItem
import me.insiro.home.server.post.dto.category.CategoryDTO
import me.insiro.home.server.post.dto.comment.CommentDTO
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.user.dto.SimpleUserDTO
import java.time.LocalDateTime
import java.util.*


data class PostResponseDTO(
    override val id: UUID,
    val title: String,
    val author: SimpleUserDTO,
    val category: CategoryDTO?,
    val status: Status,
    override val createdAt: LocalDateTime,
    val comments: List<CommentDTO>?,
    val content: String?,
    override val icon: Boolean = false
) : IResponseDTO<UUID>, IFileIcon {
    constructor(
        post: Post.Raw,
        author: SimpleUserDTO,
        content: String?,
        category: CategoryDTO? = null,
        comments: List<CommentDTO>? = null,
        icon: Boolean = false
    ) : this(
        post.id!!.value,
        post.title,
        author,
        category,
        post.status,
        post.createdAt!!,
        comments,
        content,
        icon
    )

    constructor(post: Post.Joined, content: String?, comments: List<CommentDTO>? = null, icon: Boolean) : this(
        post.id!!.value,
        post.title,
        SimpleUserDTO(post.author.id.value, post.author.name),
        post.category?.let { CategoryDTO(it) },
        post.status,
        post.createdAt!!,
        comments,
        content,
        icon
    )
}
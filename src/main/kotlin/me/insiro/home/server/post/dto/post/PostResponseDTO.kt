package me.insiro.home.server.post.dto.post

import me.insiro.home.server.application.domain.IResponseDTO
import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.post.dto.category.CategoryDTO
import me.insiro.home.server.post.dto.comment.CommentDTO
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.user.dto.SimpleUserDTO
import java.time.LocalDateTime


data class PostResponseDTO(
    override val id: Long,
    val title: String,
    val author: SimpleUserDTO,
    val category: CategoryDTO?,
    val status: Status,
    override val createdAt: LocalDateTime,
    val comments: List<CommentDTO>?
) : IResponseDTO<Long> {
    constructor(
        post: Post.Raw,
        author: SimpleUserDTO,
        category: CategoryDTO? = null,
        comments: List<CommentDTO>? = null
    ) : this(
        post.id!!.value,
        post.title,
        author,
        category,
        post.status,
        post.createdAt!!,
        comments = comments,
    )

    constructor(post: Post.Joined, comments: List<CommentDTO>? = null) : this(
        post.id!!.value,
        post.title,
        SimpleUserDTO(post.author.id.value, post.author.name),
        post.category?.let { CategoryDTO(it) },
        post.status,
        post.createdAt!!,
        comments = comments,
    )
}
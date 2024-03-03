package me.insiro.home.server.post.dto.post

import me.insiro.home.server.application.domain.Status
import me.insiro.home.server.post.dto.category.CategoryDTO
import me.insiro.home.server.post.dto.comment.CommentDTO
import me.insiro.home.server.post.entity.JoinedPost
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.user.dto.UserDTO


data class PostResponseDTO(
    val id: Long,
    val title: String,
    val author: UserDTO,
    val category: CategoryDTO,
    val status: Status,
    val comments: List<CommentDTO>
) {
    constructor(post: Post, author: UserDTO, category: CategoryDTO, comments: List<CommentDTO> = listOf()) : this(
        post.id!!.value,
        post.title,
        author,
        category,
        post.status,
        comments = comments
    )

    constructor(joinedPost: JoinedPost, comments: List<CommentDTO> = listOf()) : this(
        joinedPost.id!!.value,
        joinedPost.title,
        UserDTO.fromUser(joinedPost.author),
        CategoryDTO(joinedPost.category),
        joinedPost.status,
        comments = comments
    )
}
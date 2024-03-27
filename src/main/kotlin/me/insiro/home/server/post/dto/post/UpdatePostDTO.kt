package me.insiro.home.server.post.dto.post

import me.insiro.home.server.application.domain.dto.IModifyFileDTO
import me.insiro.home.server.application.domain.entity.Status


data class UpdatePostDTO(
    override val title: String? = null,
    val category: String? = null,
    val status: Status? = null,
    override val content: String? = null,
    override val deletedFileNames: List<String>? = null,
): IModifyFileDTO
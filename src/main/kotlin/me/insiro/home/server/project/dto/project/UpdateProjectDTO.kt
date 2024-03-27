package me.insiro.home.server.project.dto.project

import me.insiro.home.server.application.domain.dto.IModifyFileDTO
import me.insiro.home.server.application.domain.entity.Status


data class UpdateProjectDTO(
    override val title: String?,
    val status: Status?,
    override val content: String?,
    val types: List<String>?,
    override val deletedFileNames: List<String>?
): IModifyFileDTO

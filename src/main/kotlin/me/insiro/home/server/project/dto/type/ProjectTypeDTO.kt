package me.insiro.home.server.project.dto.type

import me.insiro.home.server.project.entity.ProjectType

data class ProjectTypeDTO(
    val name: String,
    val id: Int,
    val isLang: Boolean
) {
    constructor(type: ProjectType) : this(type.name, type.id!!.value, type.isLang)
}

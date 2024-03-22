package me.insiro.home.server.project.exception

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.project.entity.ProjectType
import org.springframework.http.HttpStatus

class ProjectTypeNotFoundException : AbsException {
    constructor(name: String) : super(HttpStatus.NOT_FOUND, "ProjectType Not Found (name: $name)")
    constructor(id: ProjectType.Id) : super(HttpStatus.NOT_FOUND, "ProjectType Not Found (id: ${id.value})")
}
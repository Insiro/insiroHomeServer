package me.insiro.home.server.project.exception

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.project.entity.Project
import org.springframework.http.HttpStatus

class ProjectDuplicatedException(id: Project.Id) :
    AbsException(HttpStatus.CONFLICT, "Project Duplicated (ID : ${id.value}")
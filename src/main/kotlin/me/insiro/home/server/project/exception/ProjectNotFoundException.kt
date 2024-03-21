package me.insiro.home.server.project.exception

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.project.entity.Project
import org.springframework.http.HttpStatus

class ProjectNotFoundException(val id: Project.Id) :
    AbsException(HttpStatus.NOT_FOUND, "project Not Found (Project Id : $id)")
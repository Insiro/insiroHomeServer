package me.insiro.home.server.project.exception

import me.insiro.home.server.application.exception.AbsException
import org.springframework.http.HttpStatus

class ProjectNotFoundException(title: String) :
    AbsException(HttpStatus.NOT_FOUND, "project Not Found ($title)") {
}

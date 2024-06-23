package me.insiro.home.server.project.exception

import me.insiro.home.server.application.exception.AbsException
import org.springframework.http.HttpStatus

class ProjectDuplicatedException(title:String) :
    AbsException(HttpStatus.CONFLICT, "Project Duplicated ($title)")
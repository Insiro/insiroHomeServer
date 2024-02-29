package me.insiro.home.server.user.exception

import me.insiro.home.server.application.exception.AbsException
import org.springframework.http.HttpStatus

class UserConflictExcept(name: String) : AbsException(HttpStatus.CONFLICT, "User Name Conflict ( ${name})")

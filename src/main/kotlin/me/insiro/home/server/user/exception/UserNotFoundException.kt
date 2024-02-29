package me.insiro.home.server.user.exception

import me.insiro.home.server.application.exception.AbsException
import org.springframework.http.HttpStatus

class UserNotFoundException : AbsException {
    constructor(id: Long?) : super(HttpStatus.NOT_FOUND, String.format("User Not Found ( id : %d )", id))
    constructor(name: String?) : super(HttpStatus.NOT_FOUND, String.format("User Not Found ( name : %s )", name))

}


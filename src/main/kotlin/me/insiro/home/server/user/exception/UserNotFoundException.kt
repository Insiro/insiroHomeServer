package me.insiro.home.server.user.exception

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.user.entity.User
import org.springframework.http.HttpStatus

class UserNotFoundException : AbsException {
    constructor(id: User.Id?) : super(HttpStatus.NOT_FOUND, "User Not Found ( id : $id )")
    constructor(name: String?) : super(HttpStatus.NOT_FOUND, "User Not Found ( name : %${name} )")

}


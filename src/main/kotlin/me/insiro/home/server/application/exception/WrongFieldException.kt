package me.insiro.home.server.application.exception

import org.springframework.http.HttpStatus

class WrongFieldException : AbsException(
    HttpStatus.UNPROCESSABLE_ENTITY,
    HttpStatus.UNPROCESSABLE_ENTITY.reasonPhrase
)
package me.insiro.home.server.application.exception

import org.springframework.http.HttpStatus

abstract class AbsException(val status: HttpStatus, message: String) : Exception(message)
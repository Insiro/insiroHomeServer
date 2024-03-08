package me.insiro.home.server.application.exception

import org.springframework.http.HttpStatus

class UnAuthorizedException : AbsException(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.reasonPhrase)
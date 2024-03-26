package me.insiro.home.server.file.exception

import me.insiro.home.server.application.exception.AbsException
import org.springframework.http.HttpStatus
import java.nio.file.Path

class DirCreateException(val file: Path) : AbsException(HttpStatus.INTERNAL_SERVER_ERROR, "")
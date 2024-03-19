package me.insiro.home.server.file.exception

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.file.vo.IFileItem
import org.springframework.http.HttpStatus

class FileDuplicatedException(domain: String, collection: String, name: String) :
    AbsException(HttpStatus.CONFLICT, "$domain/$collection/$name") {
    constructor(iFileItem: IFileItem) : this(iFileItem.domain, iFileItem.collection, iFileItem.name)
}
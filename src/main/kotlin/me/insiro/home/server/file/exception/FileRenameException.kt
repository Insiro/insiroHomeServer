package me.insiro.home.server.file.exception

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.file.vo.VOFileCollection
import org.springframework.http.HttpStatus

class FileRenameException(from: String, to: String) :
    AbsException(HttpStatus.INTERNAL_SERVER_ERROR, "cannot Rename file $from to $to") {
    constructor(from: VOFileCollection, to: VOFileCollection) : this(from.collection, to.collection)
}

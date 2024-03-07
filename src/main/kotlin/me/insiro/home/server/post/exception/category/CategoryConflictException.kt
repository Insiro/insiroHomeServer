package me.insiro.home.server.post.exception.category

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.post.entity.Category
import org.springframework.http.HttpStatus

class CategoryConflictException : AbsException {
    constructor(name: String) : super(HttpStatus.CONFLICT, "Category is Conflicted (name : $name)")
    constructor(id: Category.Id) : super(HttpStatus.CONFLICT, "Category is Conflicted (id : $id)")
}
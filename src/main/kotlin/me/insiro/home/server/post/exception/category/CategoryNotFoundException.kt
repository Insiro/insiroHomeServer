package me.insiro.home.server.post.exception.category

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.post.entity.Category
import org.springframework.http.HttpStatus

class CategoryNotFoundException : AbsException {
    constructor(id: Category.Id) : super(HttpStatus.NOT_FOUND, "Category Not Found (id : $id )")
    constructor(name: String) : super(HttpStatus.NOT_FOUND, "Category Not Found (name : $name )")
}
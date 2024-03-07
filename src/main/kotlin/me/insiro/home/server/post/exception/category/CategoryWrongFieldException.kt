package me.insiro.home.server.post.exception.category

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.post.entity.Category
import org.springframework.http.HttpStatus

class CategoryWrongFieldException(category: Category) : AbsException(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "failed write Category\n$category"
)
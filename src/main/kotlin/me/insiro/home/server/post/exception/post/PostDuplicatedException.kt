package me.insiro.home.server.post.exception.post

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.post.entity.Post
import org.springframework.http.HttpStatus

class PostDuplicatedException(id: Post.Id) : AbsException(HttpStatus.CONFLICT, "Post ID Duplicated (ID : ${id.value}")
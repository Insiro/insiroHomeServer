package me.insiro.home.server.post.exception.post

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.post.entity.Post
import org.springframework.http.HttpStatus

class PostNotFoundException(id: Post.Id) : AbsException(HttpStatus.NOT_FOUND, "Post Not Found (id : $id )")
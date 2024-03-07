package me.insiro.home.server.post.exception

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.post.entity.Comment
import org.springframework.http.HttpStatus

class CommentNotFoundException(id: Comment.Id) : AbsException(HttpStatus.NOT_FOUND, "Comment Not Found (Id : $id)")
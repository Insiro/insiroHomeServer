package me.insiro.home.server.post.exception.post

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.user.entity.User
import org.springframework.http.HttpStatus

class PostModifyForbiddenException(id: Post.Id, userId: User.Id) :
    AbsException(HttpStatus.FORBIDDEN, "$userId is not Writer of Post (id : $id)")
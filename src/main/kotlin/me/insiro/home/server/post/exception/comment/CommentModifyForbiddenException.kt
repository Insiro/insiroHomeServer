package me.insiro.home.server.post.exception.comment

import me.insiro.home.server.application.exception.AbsException
import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.user.entity.User
import org.springframework.http.HttpStatus

class CommentModifyForbiddenException : AbsException {
    private constructor(msg: String) : super(HttpStatus.FORBIDDEN, msg)
    constructor(id: Comment.Id, userId: User.Id?) : this("${userId ?: "anonymous"} is not Writer of comment (id : $id)")
    constructor(
        id: Comment.Id,
        userName: String
    ) : this("anonymous user $userName is not writer or wrong password (id : $id)")
}
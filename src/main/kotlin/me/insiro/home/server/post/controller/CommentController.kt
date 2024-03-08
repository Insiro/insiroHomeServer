package me.insiro.home.server.post.controller

import me.insiro.home.server.application.IController
import me.insiro.home.server.application.domain.OffsetLimit
import me.insiro.home.server.post.dto.comment.CommentDTO
import me.insiro.home.server.post.dto.comment.ModifierDTO
import me.insiro.home.server.post.dto.comment.ModifyCommentDTO
import me.insiro.home.server.post.entity.Comment
import me.insiro.home.server.post.exception.comment.CommentNotFoundException
import me.insiro.home.server.post.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("comments")
@RestController
class CommentController(private val commentService: CommentService) : IController {
    @GetMapping
    fun getComments(
        @RequestParam(required = false) offset: Long = 0,
        @RequestParam(required = false) limit: Int? = null
    ): ResponseEntity<List<CommentDTO>> {
        val offsetLimit = limit?.let { OffsetLimit(offset, limit) }
        val comments = commentService.findComments(offsetLimit = offsetLimit).map { CommentDTO(it) }
        return ResponseEntity(comments, HttpStatus.OK)
    }

    @PostMapping("{id}")
    fun appendComment(
        @PathVariable id: Comment.Id,
        @RequestParam modifyCommentDTO: ModifyCommentDTO
    ): ResponseEntity<CommentDTO> {
        val user = getSignedUser()
        val parent = commentService.getComment(id) ?: throw CommentNotFoundException(id)
        val appended = commentService.appendComment(parent, modifyCommentDTO, user)
        return ResponseEntity(CommentDTO(appended), HttpStatus.CREATED)
    }

    @GetMapping("{id}")
    fun getCommentById(
        @PathVariable id: Comment.Id,
        @RequestParam(required = false) children: Boolean = false,
        @RequestParam(required = false) offset: Long = 0,
        @RequestParam(required = false) limit: Int? = null
    ): ResponseEntity<CommentDTO> {
        var commentDTO = commentService.getComment(id)?.let(::CommentDTO) ?: throw CommentNotFoundException(id)
        if (children) {
            val offsetLimit = limit?.let { OffsetLimit(offset, limit) }
            val childrenDTO = commentService.findComments(parent = id, offsetLimit = offsetLimit).map(::CommentDTO)
            commentDTO = commentDTO.copy(children = childrenDTO)
        }
        return ResponseEntity(commentDTO, HttpStatus.OK)
    }

    @PatchMapping("{id}")
    fun updateComment(
        @PathVariable id: Comment.Id,
        @RequestBody modifyCommentDTO: ModifyCommentDTO
    ): ResponseEntity<CommentDTO> {
        val user = getSignedUser()
        val commentDTO = commentService.updateComment(id, modifyCommentDTO, user)?.let(::CommentDTO)
            ?: throw CommentNotFoundException(id)
        return ResponseEntity(commentDTO, HttpStatus.OK)
    }

    @DeleteMapping("{id}")
    fun deleteComment(@PathVariable id: Comment.Id, @RequestBody modifierDTO: ModifierDTO): ResponseEntity<String> {
        val user = getSignedUser()
        commentService.deleteComment(id, modifierDTO, user)
        return ResponseEntity("Success", HttpStatus.OK)
    }


}
package me.insiro.home.server.post.controller

import me.insiro.home.server.post.dto.comment.CommentDTO
import me.insiro.home.server.post.dto.comment.ModifyCommentDTO
import me.insiro.home.server.post.dto.post.NewPostDTO
import me.insiro.home.server.post.dto.post.PostResponseDTO
import me.insiro.home.server.post.dto.post.UpdatePostDTO
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.post.exception.category.CategoryNotFoundException
import me.insiro.home.server.post.exception.post.PostNotFoundException
import me.insiro.home.server.post.service.CategoryService
import me.insiro.home.server.post.service.CommentService
import me.insiro.home.server.post.service.PostService
import me.insiro.home.server.user.dto.AuthDetail
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("posts")
class PostController(
    private val postService: PostService,
    private val categoryService: CategoryService,
    val commentService: CommentService,
) {
    fun getSignedUser(): AuthDetail {
        return SecurityContextHolder.getContext().authentication.principal as AuthDetail
    }

    @GetMapping
    fun getPosts(): ResponseEntity<List<PostResponseDTO>> {
        val posts = postService.findPosts().map { PostResponseDTO(it) }
        return ResponseEntity(posts, HttpStatus.OK)
    }

    @Secured("ROLE_WRITER")
    @PostMapping
    fun createPost(
        @RequestBody newPostDTO: NewPostDTO,
    ): ResponseEntity<PostResponseDTO> {
        val categoryId = newPostDTO.category?.let {
            categoryService.findByName(it) ?: throw CategoryNotFoundException(it)
        }?.id


        val post = postService.createPost(newPostDTO, categoryId, getSignedUser().user)
        return ResponseEntity(PostResponseDTO(post), HttpStatus.CREATED)
    }


    @GetMapping("{id}")
    fun getPost(
        @PathVariable id: Post.Id,
        @RequestParam(required = false) comment: Boolean = false
    ): ResponseEntity<PostResponseDTO> {
        val post = postService.findPost(id) ?: throw PostNotFoundException(id)
        val comments = commentService.findComments(id).map { CommentDTO(it) }

        return ResponseEntity(PostResponseDTO(post, comments), HttpStatus.OK)
    }

    @Secured("ROLE_WRITER")
    @PatchMapping("{id}")
    fun updatePost(
        @PathVariable id: Post.Id,
        @RequestBody updateDTO: UpdatePostDTO,
    ): ResponseEntity<PostResponseDTO> {

        val categoryId = updateDTO.category?.let {
            categoryService.findByName(it) ?: throw CategoryNotFoundException(it)
        }?.id
        val post =
            postService.updatePost(id, updateDTO, categoryId, getSignedUser().user) ?: throw PostNotFoundException(id)
        return ResponseEntity(PostResponseDTO(post), HttpStatus.OK)
    }

    @DeleteMapping("{id}")
    fun deletePost(
        @PathVariable id: Post.Id,
    ): ResponseEntity<Boolean> {
        val result = postService.deletePost(id, getSignedUser().user)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("{id}/comments")
    fun getComments(@PathVariable id: Post.Id): ResponseEntity<List<CommentDTO>> {
        val comments = commentService.findComments(id).map { CommentDTO(it) }
        return ResponseEntity(comments, HttpStatus.OK)
    }

    @Secured("ROLE_USER")
    @PostMapping("{id}/comments/signed")
    fun addComment(
        @PathVariable id: Post.Id,
        @RequestBody newCommentDTO: ModifyCommentDTO.Signed
    ): ResponseEntity<CommentDTO> {
        val user = getSignedUser().user
        postService.findPost(id) ?: throw PostNotFoundException(id)
        val comment = commentService.addComment(id, newCommentDTO, user)
        return ResponseEntity(CommentDTO(comment), HttpStatus.CREATED)
    }

    @PostMapping("{id}/comments")
    fun addComment(
        @PathVariable id: Post.Id,
        @RequestBody newCommentDTO: ModifyCommentDTO.Anonymous
    ): ResponseEntity<CommentDTO> {
        postService.findPost(id) ?: throw PostNotFoundException(id)
        val comment = commentService.addComment(id, newCommentDTO)
        return ResponseEntity(CommentDTO(comment), HttpStatus.CREATED)
    }
}
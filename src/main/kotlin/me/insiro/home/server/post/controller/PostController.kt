package me.insiro.home.server.post.controller

import me.insiro.home.server.application.IController
import me.insiro.home.server.application.domain.OffsetLimit
import me.insiro.home.server.post.dto.category.CategoryDTO
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
import me.insiro.home.server.user.dto.SimpleUserDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("posts")
class PostController(
    private val postService: PostService,
    private val categoryService: CategoryService,
    val commentService: CommentService,
) : IController {

    @GetMapping
    fun getPosts(): ResponseEntity<List<PostResponseDTO>> {
        val posts = postService.findJoinedPosts().map { PostResponseDTO(it) }
        return ResponseEntity(posts, HttpStatus.OK)
    }

    @Secured("ROLE_WRITER")
    @PostMapping
    fun createPost(
        @RequestBody newPostDTO: NewPostDTO,
    ): ResponseEntity<PostResponseDTO> {
        val category = newPostDTO.category?.let {
            categoryService.findByName(it) ?: throw CategoryNotFoundException(it)
        }

        val user = getSignedUser()!!
        val post = postService.createPost(newPostDTO, user, category?.id)
        return ResponseEntity(
            PostResponseDTO(post, SimpleUserDTO(user), category?.let { CategoryDTO(it) }),
            HttpStatus.CREATED
        )
    }


    @GetMapping("{id}")
    fun getPost(
        @PathVariable id: Post.Id,
        @RequestParam(required = false) comment: Boolean = false,
        @RequestParam(required = false) offset: Long = 0,
        @RequestParam(required = false) limit: Int? = null
    ): ResponseEntity<PostResponseDTO> {
        val offsetLimit = limit?.let { OffsetLimit(offset, limit) }
        val post = postService.findJoinedPost(id) ?: throw PostNotFoundException(id)
        val comments = commentService.findComments(id, offsetLimit).map { CommentDTO(it) }

        return ResponseEntity(PostResponseDTO(post, comments), HttpStatus.OK)
    }

    @Secured("ROLE_WRITER")
    @PatchMapping("{id}")
    fun updatePost(
        @PathVariable id: Post.Id,
        @RequestBody updateDTO: UpdatePostDTO,
    ): ResponseEntity<PostResponseDTO> {

        val category =
            updateDTO.category?.let { categoryService.findByName(it) ?: throw CategoryNotFoundException(it) }
        val user = getSignedUser()!!
        val post =
            postService.updatePost(id, updateDTO, category?.id, user) ?: throw PostNotFoundException(id)
        return ResponseEntity(
            PostResponseDTO(post, SimpleUserDTO(user), category?.let { CategoryDTO(it) }),
            HttpStatus.OK
        )
    }

    @DeleteMapping("{id}")
    fun deletePost(
        @PathVariable id: Post.Id,
    ): ResponseEntity<Boolean> {
        val result = postService.deletePost(id, getSignedUser()!!)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("{id}/comments")
    fun getComments(
        @PathVariable id: Post.Id,
        @RequestParam(required = false) offset: Long = 0,
        @RequestParam(required = false) limit: Int? = null
    ): ResponseEntity<List<CommentDTO>> {
        val offsetLimit = limit?.let { OffsetLimit(offset, limit) }
        val comments = commentService.findComments(id, offsetLimit).map { CommentDTO(it) }
        return ResponseEntity(comments, HttpStatus.OK)
    }

    @PostMapping("{id}/comments/signed")
    fun addComment(
        @PathVariable id: Post.Id,
        @RequestBody newCommentDTO: ModifyCommentDTO.Signed
    ): ResponseEntity<CommentDTO> {
        val user = getSignedUser()
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
package me.insiro.home.server.post.controller

import me.insiro.home.server.application.ISignedController
import me.insiro.home.server.application.domain.dto.OffsetLimit
import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.file.service.PostFileService
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
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("posts")
class PostController(
    private val postService: PostService,
    private val categoryService: CategoryService,
    private val fileService: PostFileService,
    val commentService: CommentService,
) : ISignedController {

    @GetMapping
    fun getPosts(
        @RequestParam(required = false) offset: Long = 0,
        @RequestParam(required = false) limit: Int? = null,
        @RequestParam(required = false) status: List<Status> = arrayListOf(Status.PUBLISHED)
    ): ResponseEntity<List<PostResponseDTO>> {
        val offsetLimit = limit?.let { OffsetLimit(offset, limit) }
        val posts = postService.findJoinedPosts(status = status, offsetLimit = offsetLimit).map { PostResponseDTO(it) }
        return ResponseEntity(posts, HttpStatus.OK)
    }

    @Secured("ROLE_WRITER")
    @PostMapping
    fun createPost(
        @RequestPart("data") newPostDTO: NewPostDTO,
        @RequestParam("files") files: List<MultipartFile>?
    ): ResponseEntity<PostResponseDTO> {
        val category = newPostDTO.category?.let {
            categoryService.findByName(it) ?: throw CategoryNotFoundException(it)
        }
        val user = getSignedUser().getOrThrow()
        val post = postService.createPost(newPostDTO, user, category?.id).getOrThrow()

        fileService.create(post, newPostDTO.content, files)

        return ResponseEntity(
            PostResponseDTO(post, SimpleUserDTO(user), category?.let { CategoryDTO(it) }),
            HttpStatus.CREATED
        )
    }


    @GetMapping("{id}")
    fun getPost(
        @PathVariable id: Post.Id,
        @RequestParam(required = false) comment: Boolean? = false,
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
        @RequestPart("data") updateDTO: UpdatePostDTO,
        @RequestParam("files") files: List<MultipartFile>?
    ): ResponseEntity<PostResponseDTO> {
        val category =
            updateDTO.category?.let { categoryService.findByName(it) ?: throw CategoryNotFoundException(it) }
        val user = getSignedUser().getOrThrow()
        val post =
            postService.updatePost(id, updateDTO, category?.id, user) ?: throw PostNotFoundException(id)
        fileService.update(post, updateDTO, files)
        return ResponseEntity(
            PostResponseDTO(post, SimpleUserDTO(user), category?.let { CategoryDTO(it) }),
            HttpStatus.OK
        )
    }

    @DeleteMapping("{id}")
    fun deletePost(
        @PathVariable id: Post.Id,
    ): ResponseEntity<Boolean> {
        val post = postService.findPost(id) ?: throw PostNotFoundException(id)
        val result = postService.deletePost(id, getSignedUser().getOrThrow())
        fileService.delete(post)
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

    @PostMapping("{id}/comments")
    fun addComment(
        @PathVariable id: Post.Id,
        @RequestBody newCommentDTO: ModifyCommentDTO,
    ): ResponseEntity<CommentDTO> {
        val user = getSignedUser().getOrNull()
        postService.findPost(id) ?: throw PostNotFoundException(id)
        val comment = commentService.addComment(id, newCommentDTO, user)
        return ResponseEntity(CommentDTO(comment), HttpStatus.CREATED)
    }

    @GetMapping("categories/{categoryName}")
    fun getPostsByCategory(
        @PathVariable categoryName: String,
        @RequestParam(required = false) offset: Long = 0,
        @RequestParam(required = false) limit: Int? = null,
        @RequestParam(required = false) status: List<Status> = arrayListOf(Status.PUBLISHED)
    ): ResponseEntity<List<PostResponseDTO>> {
        val category = categoryService.findByName(categoryName) ?: throw CategoryNotFoundException(categoryName)
        val offsetLimit = limit?.let { OffsetLimit(offset, limit) }
        val posts = postService.findJoinedPosts(category.id, status, offsetLimit).map { PostResponseDTO(it) }
        return ResponseEntity(posts, HttpStatus.OK)
    }

}
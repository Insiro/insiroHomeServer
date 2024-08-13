package me.insiro.home.server.application

import me.insiro.home.server.application.config.ApplicationOptions
import me.insiro.home.server.application.domain.dto.ApplicationStatus
import me.insiro.home.server.application.domain.dto.SearchDTO
import me.insiro.home.server.post.dto.post.PostResponseDTO
import me.insiro.home.server.post.service.PostService
import me.insiro.home.server.project.dto.project.ProjectDTO
import me.insiro.home.server.project.service.ProjectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class RootController(
    val option: ApplicationOptions,
    private val postService: PostService,
    private val projectService: ProjectService
) {

    @GetMapping("status")
    fun applicationStatus(): ApplicationStatus {
        return ApplicationStatus(option)
    }

    @GetMapping("/search")
    fun serverSearch(@RequestParam("kq") query: String): ResponseEntity<SearchDTO> {
        return ResponseEntity.ok(
            SearchDTO(
                projects = projectService.findJoined(keywords = query).map(::ProjectDTO),
                posts = postService.findJoinedPosts(keywords = query).map { PostResponseDTO(it, null) }
            )
        )
    }
}
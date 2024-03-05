package me.insiro.home.server.post.service

import me.insiro.home.server.post.dto.post.NewPostDTO
import me.insiro.home.server.post.dto.post.UpdatePostDTO
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.entity.JoinedPost
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.user.entity.User
import org.springframework.stereotype.Service

@Service
class PostService {
    fun createPost(createDTO: NewPostDTO, categoryId: Category.Id?, user: User): JoinedPost {
        TODO("Not Yet Implemented")
    }

    fun updatePost(id: Post.Id, updateDTO: UpdatePostDTO, categoryId: Category.Id?, user: User): JoinedPost? {
        TODO("Not Yet Implemented")
    }

    fun deletePost(id: Post.Id, user: User): Boolean {
        TODO("Not Yet Implemented")
    }

    fun findPost(id: Post.Id): JoinedPost? {
        TODO("Not Yet Implemented")
    }

    fun findPosts(): List<JoinedPost> {
        TODO("Not Yet Implemented")
    }

    fun findPostByCategory(id: Category.Id): List<JoinedPost> {
        TODO("Not Yet Implemented")
    }

    fun changeCategory(id: Category.Id, newId: Category.Id?): JoinedPost? {
        TODO("Not Yet Implemented")
    }

}


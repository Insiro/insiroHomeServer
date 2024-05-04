package me.insiro.home.server.post.service

import me.insiro.home.server.application.domain.entity.Status
import me.insiro.home.server.post.dto.post.NewPostDTO
import me.insiro.home.server.post.dto.post.UpdatePostDTO
import me.insiro.home.server.post.entity.Categories
import me.insiro.home.server.post.entity.Category
import me.insiro.home.server.post.entity.Post
import me.insiro.home.server.post.entity.Posts
import me.insiro.home.server.post.exception.post.PostNotFoundException
import me.insiro.home.server.post.repository.PostRepository
import me.insiro.home.server.testUtils.AbsDataBaseTest
import me.insiro.home.server.testUtils.DBInserter
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.entity.Users
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PostServiceTest : AbsDataBaseTest(Users, Categories, Posts) {
    private lateinit var user: User
    private lateinit var post: Post
    private lateinit var category: Category
    private val postRepository = PostRepository()
    private val postService = PostService(postRepository)

    @BeforeEach
    fun initTest() {
        resetDataBase()
        user = DBInserter.insertUser(User("testUser", "testPwd", "testEmail", 0b1))
        category = DBInserter.insertCategory(Category("TEST_CATEGORY"))
        post = DBInserter.insertPost(Post.Raw("testPost", Status.PUBLISHED, user.id!!, category.id))
    }

    @Test
    fun `test insert Post and Get`() {
        //test Conflict
        val newPost = NewPostDTO("newPost", null, "postContent", status = Status.PUBLISHED)
        val created = postService.createPost(newPost, user, null).getOrThrow()
        val found = postService.findPost(created.id!!).getOrThrow()
        assertEquals(newPost.title, found.title)
        assertEquals(newPost.status, found.status)
    }

    @Test
    fun updatePost() {
        val cate2 = DBInserter.insertCategory(Category("testCate2"))
        val updatePostDTO = UpdatePostDTO("newTitle", category = cate2.name)
        val updated = postService.updatePost(post.id!!, updatePostDTO, cate2.id, user).getOrThrow()
        assertEquals(cate2.id, updated.categoryId)
        assertEquals(updatePostDTO.title, updated.title)
    }

    @Test
    fun `test delete Post and find return null`() {
        postService.deletePost(post.id!!, user)
        assertThrows<PostNotFoundException> { postService.findPost(post.id!!).getOrThrow() }
    }

    @Test
    fun findPostByCategory() {
        val posts = postService.findJoinedPosts(category.id!!)
        assertEquals(1, posts.size)
    }

    @Test
    fun changeCategory() {
        val nChanged = postService.changeCategoryOfPosts(category.id!!, null)
        assertEquals(1, nChanged)
        assertNull(postRepository.findById(post.id!!)?.categoryId)
    }
}
package me.insiro.home.server.user.controller

import me.insiro.home.server.application.domain.OffsetLimit
import me.insiro.home.server.user.UserService
import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.dto.UserDTO
import me.insiro.home.server.user.entity.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("users")
class UserController(private val userService: UserService) {
    @GetMapping
    fun getAllUser(
        @RequestParam(required = false) offset: Long = 0,
        @RequestParam(required = false) limit: Int? = null
    ): ResponseEntity<List<UserDTO>> {
        val offsetLimit = limit?.let { OffsetLimit(offset, limit) }
        val users = userService.getUsers(offsetLimit).map(UserDTO.Companion::fromUser)
        return ResponseEntity(users, HttpStatus.OK)
    }

    @PostMapping
    fun createUser(@RequestBody newUserDTO: NewUserDTO): ResponseEntity<UserDTO> {
        val user = userService.createUser(newUserDTO)
        return ResponseEntity(UserDTO.fromUser(user), HttpStatus.CREATED)
    }

    @GetMapping("{id}")
    fun getUser(@PathVariable id: User.Id): ResponseEntity<UserDTO> {
        val user = userService.getUser(id).getOrThrow()
        return ResponseEntity(UserDTO.fromUser(user), HttpStatus.OK)
    }

    @PatchMapping("{id}")
    fun updateUser(@PathVariable id: User.Id, @RequestBody updateUserDTO: UpdateUserDTO): ResponseEntity<UserDTO> {
        val user = userService.updateUser(id, updateUserDTO).getOrThrow()
        return ResponseEntity(UserDTO.fromUser(user), HttpStatus.OK)
    }

    @DeleteMapping("{id}")
    fun deleteUser(@PathVariable id: User.Id): String {
        userService.deleteUser(id)
        return "success"
    }
}

package me.insiro.home.server.user

import me.insiro.home.server.application.exception.UserNotFoundException
import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.dto.UserDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("users")
class UserController(private val userService: UserService) {
    @GetMapping
    fun getAllUser(@RequestParam(required = false) offset: Long = 0, @RequestParam(required = false) limit: Int? = null): ResponseEntity<List<UserDTO>> {
        val users = userService.getUsers(offset, limit).map(UserDTO.Companion::fromUser)
        return ResponseEntity(users, HttpStatus.OK)
    }

    @PostMapping
    fun createUser(@RequestBody newUserDTO: NewUserDTO): ResponseEntity<UserDTO> {
        val user = userService.createUser(newUserDTO)
        return ResponseEntity(UserDTO.fromUser(user), HttpStatus.CREATED)
    }

    @GetMapping("{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<UserDTO> {
        val user = userService.getUser(id) ?: throw UserNotFoundException(id)
        return ResponseEntity(UserDTO.fromUser(user), HttpStatus.OK)
    }

    @PatchMapping("{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody updateUserDTO: UpdateUserDTO): ResponseEntity<UserDTO> {
        val user = userService.updateUser(id, updateUserDTO)
        return ResponseEntity(UserDTO.fromUser(user), HttpStatus.OK)
    }

    @DeleteMapping("{id}")
    fun deleteUser(@PathVariable id: Long): String {
        userService.deleteUser(id)
        return "success"
    }
}

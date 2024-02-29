package me.insiro.home.server.user

import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.entity.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder){
    fun getUser(id: Long): User? {
        TODO("Not yet implemented")
    }

    fun updateUser(id: Long, updateUserDTO: UpdateUserDTO): User? {
        TODO("Not yet implemented")
    }

    fun deleteUser(id: Long?): Boolean {
        TODO("Not yet implemented")
    }

    fun createUser(newUserDTO: NewUserDTO): User {
        TODO("Not yet implemented")
    }

    fun getUsers(offset: Int = 0, limit: Long?= null): List<User> {
        TODO("Not yet implemented")
    }
}
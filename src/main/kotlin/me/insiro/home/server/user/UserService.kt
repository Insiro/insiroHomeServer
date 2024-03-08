package me.insiro.home.server.user

import me.insiro.home.server.application.domain.OffsetLimit
import me.insiro.home.server.user.dto.AuthDetail
import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {

    fun getUser(id: User.Id): User? {
        return userRepository.findById(id)
    }

    fun getUser(userName: String): User? {
        return userRepository.find(userName)
    }

    override fun loadUserByUsername(username: String): AuthDetail? {
        val user = getUser(username) ?: return null
        return AuthDetail(user)
    }

    fun updateUser(id: User.Id, updateUserDTO: UpdateUserDTO): User? {
        val newPwd = updateUserDTO.password?.let { passwordEncoder.encode(it) }
        return userRepository.update(
            id,
            name = updateUserDTO.name,
            email = updateUserDTO.email,
            password = newPwd
        )
    }

    fun deleteUser(id: User.Id): Boolean {
        return userRepository.delete(id)
    }

    fun createUser(newUserDTO: NewUserDTO): User {
        return userRepository.new(
            User(
                newUserDTO.name,
                passwordEncoder.encode(newUserDTO.password),
                newUserDTO.email,
                UserRole.ROLE_USER.key,
                createdAt = LocalDateTime.now()
            )
        )
    }

    fun getUsers(offsetLimit: OffsetLimit?): List<User> {
        return userRepository.find(offsetLimit)
    }
}
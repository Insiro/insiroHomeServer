package me.insiro.home.server.user

import me.insiro.home.server.application.domain.OffsetLimit
import me.insiro.home.server.user.dto.AuthDetail
import me.insiro.home.server.user.dto.NewUserDTO
import me.insiro.home.server.user.dto.UpdateUserDTO
import me.insiro.home.server.user.dto.UserRole
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.exception.UserNotFoundException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {

    fun getUser(id: User.Id): Result<User> {
        return userRepository.findById(id)?.let { Result.success(it) } ?: Result.failure(UserNotFoundException(id))
    }

    fun getUser(userName: String): Result<User> {
        return userRepository.find(userName)
            ?.let { Result.success(it) }
            ?: Result.failure(UserNotFoundException(userName))
    }

    override fun loadUserByUsername(username: String): AuthDetail {
        return AuthDetail(getUser(username).getOrThrow())
    }

    fun updateUser(id: User.Id, updateUserDTO: UpdateUserDTO): Result<User> {
        return userRepository.update(
            id,
            name = updateUserDTO.name,
            email = updateUserDTO.email,
            password = updateUserDTO.password?.let { passwordEncoder.encode(it) }
        )?.let { Result.success(it) } ?: Result.failure(UserNotFoundException(id))
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
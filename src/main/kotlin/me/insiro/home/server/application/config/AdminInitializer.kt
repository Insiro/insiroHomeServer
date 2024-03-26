package me.insiro.home.server.application.config

import me.insiro.home.server.user.UserRepository
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.utils.PasswordEncoder
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
class AdminInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val adminProperties: AdminAccountProperties,
) :
    CommandLineRunner {

    override fun run(vararg args: String?) = transaction {
        if ("resetAdmin" in args)
            resetAdmin()
        if (userRepository.find(adminProperties.name) == null)
            resetAdmin()
    }

    private fun resetAdmin() {

        val newUser =
            User(
                adminProperties.name,
                passwordEncoder.encode(adminProperties.password),
                adminProperties.email,
                -1,
                User.Id(-1)
            )
        userRepository.upsertById(newUser)
    }
}

@ConfigurationProperties("app.admin")
data class AdminAccountProperties(
    var name: String = "administrator",
    val password: String = "administrator",
    val email: String = ""
)
package me.insiro.home.server.application.config.intialize

import me.insiro.home.server.application.config.ApplicationOptions
import me.insiro.home.server.user.UserRepository
import me.insiro.home.server.user.entity.User
import me.insiro.home.server.user.utils.PasswordEncoder
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class ApplicationInitializer(
    val option: ApplicationOptions,
    val environment: Environment,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val adminProperties: AdminAccountProperties,
) :
    ApplicationRunner {
    override fun run(args: ApplicationArguments?) = transaction {
        val profile = environment.activeProfiles
        option.env =  if(profile.isNotEmpty())profile[0]  else "default"
        val initOption = ApplicationInitializeOption()
        args?.let {
            initOption.resetAdmin = it.containsOption("resetAdmin")
        }

        if (initOption.resetAdmin || userRepository.find(adminProperties.name) == null)
            resetAdmin()
        Unit
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

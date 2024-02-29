package me.insiro.home.server.user.dto

import me.insiro.home.server.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthDetail(val user: User) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val roles = UserRole.fromPermissionKey(user.permission)
        val authorities = roles.map { GrantedAuthority(it::name) }.toMutableList()
        return authorities
    }


    override fun getPassword(): String {
        return user.hashedPassword
    }

    override fun getUsername(): String {
        return user.name
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return false
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}
package me.insiro.home.server.user.dto

import me.insiro.home.server.user.entity.User

enum class UserRole(val key: Int) {
    ROLE_ADMIN(-1), ROLE_USER(0), ROLE_WRITER(0x1);

    private fun isGranted(permissionKey: Int): Boolean {
        return (key and permissionKey) == key
    }

    fun isGranted(user: User?): Boolean {
        user ?: return false
        return isGranted(user.permission)
    }

    companion object {
        fun fromPermissionKey(key: Int): List<UserRole> {
            if (key == -1)
                return enumValues<UserRole>().toList()
            val roles = ArrayList<UserRole>()
            for (role in enumValues<UserRole>().toList()) {
                if (role.isGranted(key)) roles.add(role)
            }
            return roles
        }

        fun toPermissionKey(roles: List<UserRole>): Int {
            if (roles.isEmpty()) return 0
            var key = 0
            for (item in roles) {
                when (item) {
                    ROLE_ADMIN -> return -1
                    ROLE_WRITER -> key = key or ROLE_WRITER.key
                    ROLE_USER -> {}
                }
            }
            return key
        }
    }
}
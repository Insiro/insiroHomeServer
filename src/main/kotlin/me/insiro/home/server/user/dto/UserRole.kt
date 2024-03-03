package me.insiro.home.server.user.dto

enum class UserRole(val key: Int) {
    ROLE_ADMIN(-1), ROLE_USER(0), ROLE_WRITER(0x1);

    companion object {
        fun fromPermissionKey(key: Int): List<UserRole> {
            val roles = arrayListOf(ROLE_USER)
            when (key) {
                -1 -> return enumValues<UserRole>().toList()
                0 -> {}
                else -> {
                    if ((key and ROLE_WRITER.key) == ROLE_WRITER.key) roles.add(ROLE_WRITER)
                }
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
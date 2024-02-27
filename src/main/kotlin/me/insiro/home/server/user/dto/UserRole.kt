package me.insiro.home.server.user.dto

enum class UserRole(val key:Int) {
    ROLE_ADMIN(-1), ROLE_READ_ONLY(0), ROLE_WRITER(0x1);

    companion object {
        fun fromPermissionKey(key: Int): List<UserRole> {
            return when (key) {
                -1 -> arrayListOf(ROLE_ADMIN)
                0 -> arrayListOf(ROLE_READ_ONLY)
                else -> {
                    val roles = ArrayList<UserRole>()
                    if ((key and ROLE_WRITER.key) == ROLE_WRITER.key) roles.add(ROLE_WRITER)
                    roles
                }
            }
        }

        fun toPermissionKey(roles: List<UserRole>): Int {
            if (roles.isEmpty()) return -1
            var key = 0
            for (item in roles) {
                when (item) {
                    ROLE_ADMIN -> return -1
                    ROLE_READ_ONLY -> return 0
                    ROLE_WRITER -> key = key or ROLE_WRITER.key
                }
            }
            return key
        }
    }
}
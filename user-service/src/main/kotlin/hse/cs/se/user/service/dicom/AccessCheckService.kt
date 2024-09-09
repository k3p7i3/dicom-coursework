package hse.cs.se.user.service.dicom

import hse.cs.se.user.service.user.UserService
import java.security.Principal

class AccessCheckService(
    private val userService: UserService
) {
    fun hasAccessToResource(
        principal: Principal,
        filePath: String
    ): Boolean {
        val domain = filePath.substringBefore("/")
        val userId = userService.getUserId(principal.name).toString()

        return domain == userId
    }

    fun getDomain(username: String? = null): String {
        return userService.getUserId(
            username ?: userService.getCurrentUserName()
        ).toString()
    }
}

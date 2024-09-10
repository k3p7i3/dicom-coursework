package hse.cs.se.user.service.dicom

import hse.cs.se.user.service.user.UserService
import hse.cs.se.user.service.user.data.model.User
import org.springframework.stereotype.Component
import java.security.Principal

@Component
class AccessCheckService(
    private val userService: UserService
) {
    fun hasAccessToResource(
        user: User,
        filePath: String
    ): Boolean {
        val domain = filePath.substringBefore("/")
        val userId = userService.getUserId(user.email).toString()

        return domain == userId
    }

    fun getDomain(username: String? = null): String {
        return userService.getUserId(
            username ?: userService.getCurrentUserName()
        ).toString()
    }
}

package hse.cs.se.user.service.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException(
    userEmail: String,
    override val cause: Throwable? = null
): RuntimeException(
    "User $userEmail does not exist",
    cause
)
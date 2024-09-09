package hse.cs.se.user.service.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class PasswordsNotMatchException(
    userEmail: String,
    override val cause: Throwable? = null
): RuntimeException(
    "Passwords don't match",
    cause
)
package hse.cs.se.user.service.user.data.model.web

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class CreateUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)
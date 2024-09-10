package hse.cs.se.user.service.user.data.model.web

data class CreateUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)
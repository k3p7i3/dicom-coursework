package hse.cs.se.user.service.user.data.model.web

data class CurrentUser(
    val domain: String,
    val firstName: String,
    val lastName: String,
    val email: String
)
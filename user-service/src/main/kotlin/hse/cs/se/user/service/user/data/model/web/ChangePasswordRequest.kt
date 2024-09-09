package hse.cs.se.user.service.user.data.model.web

data class ChangePasswordRequest(
    val email: String,
    val oldPassword: String,
    val newPassword: String
)

package hse.cs.se.user.service.user.data.model.web

data class EditUserRequest(
    val userEmail: String,
    val edit: UserEdit
) {
    data class UserEdit(
        val email: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
    )
}
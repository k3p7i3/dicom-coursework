package hse.cs.se.user.service.user

import hse.cs.se.user.service.user.data.model.web.ChangePasswordRequest
import hse.cs.se.user.service.user.data.model.web.CreateUserRequest
import hse.cs.se.user.service.user.data.model.web.CurrentUser
import hse.cs.se.user.service.user.data.model.web.EditUserRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/get")
    @PreAuthorize("authentication.principal.email == #email")
    fun getUser(@RequestParam email: String? = null): ResponseEntity<CurrentUser> {
        return ResponseEntity.ok(userService.getCurrentUser(email))
    }

    @PostMapping("/create")
    fun createNewUser(
        @RequestBody createUserRequest: CreateUserRequest
    ): ResponseEntity<CurrentUser> {
        return ResponseEntity.ok(userService.createNewUser(createUserRequest))
    }

    @PostMapping("/edit")
    @PreAuthorize("authentication.principal.email == #editUserRequest.userEmail")
    fun editUser(
        @RequestBody editUserRequest: EditUserRequest
    ): ResponseEntity<CurrentUser> {
        return ResponseEntity.ok(userService.editUser(editUserRequest))
    }

    @PostMapping("/change-password")
    @PreAuthorize("authentication.principal.email == #changePasswordRequest.email")
    fun changePassword(
        @RequestBody changePasswordRequest: ChangePasswordRequest
    ): ResponseEntity<CurrentUser> {
        return ResponseEntity.ok(userService.changePassword(changePasswordRequest))
    }
}

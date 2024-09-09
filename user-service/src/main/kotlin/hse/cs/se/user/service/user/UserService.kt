package hse.cs.se.user.service.user

import hse.cs.se.user.service.dicom.DicomStorageClient
import hse.cs.se.user.service.user.data.model.User
import hse.cs.se.user.service.user.data.model.web.ChangePasswordRequest
import hse.cs.se.user.service.user.data.model.web.CreateUserRequest
import hse.cs.se.user.service.user.data.model.web.CurrentUser
import hse.cs.se.user.service.user.data.model.web.EditUserRequest
import hse.cs.se.user.service.user.data.repository.UserRepository
import hse.cs.se.user.service.user.exception.PasswordsNotMatchException
import hse.cs.se.user.service.user.exception.UserAlreadyExistException
import hse.cs.se.user.service.user.exception.UserNotFoundException
import hse.cs.se.user.service.utils.logError
import hse.cs.se.user.service.utils.logTrace
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val dicomStorageClient: DicomStorageClient
) {
    fun getCurrentUserName(): String =
        SecurityContextHolder.getContext().authentication.name

    fun getUserId(email: String): UUID =
        userRepository.findByEmail(email)?.uid
            ?: throw UserNotFoundException(email)

    fun getCurrentUser(email: String?): CurrentUser =
        userRepository.findByEmail(
            email ?: getCurrentUserName()
        )?.let {
            CurrentUser(
                domain = it.uid.toString(),
                firstName = it.firstName,
                lastName = it.lastName,
                email = it.email
            )
        }
            ?: throw UserNotFoundException(email ?: getCurrentUserName())

    fun createNewUser(createUserRequest: CreateUserRequest): CurrentUser {
        val alreadyExists = userRepository.existsByEmail(createUserRequest.email)

        if (alreadyExists) {
            throw UserAlreadyExistException(createUserRequest.email)
        }

        val user = userRepository.save(
            User(
                firstName = createUserRequest.firstName,
                lastName = createUserRequest.lastName,
                email = createUserRequest.email,
                userPassword = passwordEncoder.encode(createUserRequest.password)
            )
        )
        logger.logTrace("Created user(id=${user.uid}, email=${user.email})")

        val response = dicomStorageClient.createDirectory("/${user.uid}")
        if (response.statusCode != HttpStatus.OK) {
            logger.logError(
                "Failed to create directory for user " +
                    "$user.email files: ${response.statusCode}"
            )
        }

        return CurrentUser(
            user.uid.toString(),
            user.firstName,
            user.lastName,
            user.email
        )
    }

    fun editUser(editUserRequest: EditUserRequest): CurrentUser {
        val user = userRepository.findByEmail(
            editUserRequest.userEmail
        ) ?: throw UserNotFoundException(editUserRequest.userEmail)

        editUserRequest.edit.email?.let { user.email = it }
        editUserRequest.edit.firstName?.let { user.firstName = it }
        editUserRequest.edit.lastName?.let { user.lastName = it }

        val updatedUser = userRepository.save(user)

        logger.logTrace("Successfully updated user(id=${user.uid})")

        return CurrentUser(
            updatedUser.uid.toString(),
            updatedUser.firstName,
            updatedUser.lastName,
            updatedUser.email
        )
    }

    fun changePassword(changePasswordRequest: ChangePasswordRequest): CurrentUser {
        val user = userRepository.findByEmail(
            changePasswordRequest.email
        ) ?: throw UserNotFoundException(changePasswordRequest.email)

        if (user.userPassword != passwordEncoder.encode(changePasswordRequest.oldPassword)) {
            throw PasswordsNotMatchException(changePasswordRequest.email)
        }

        user.userPassword = passwordEncoder.encode(changePasswordRequest.newPassword)
        userRepository.save(user)
        logger.logTrace(
            "Successfully changed password for user ${changePasswordRequest.email}"
        )

        return CurrentUser(
            user.uid.toString(),
            user.firstName,
            user.lastName,
            user.email
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserService::class.java)
    }
}
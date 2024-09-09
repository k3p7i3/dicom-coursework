package hse.cs.se.user.service.user

import hse.cs.se.user.service.user.data.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails =
        username?.let { username ->
            userRepository.findByEmail(username)
                ?: throw UsernameNotFoundException(
                    "User with email $username does not exists"
                )
        } ?: throw UsernameNotFoundException("Username must not be empty")
}
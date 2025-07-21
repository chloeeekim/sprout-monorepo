package chloe.sprout.backend.auth

import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            ?: throw UserNotFoundException()

        return CustomUserDetails(user)
    }
}
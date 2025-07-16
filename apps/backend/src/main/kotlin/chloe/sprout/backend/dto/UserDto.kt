package chloe.sprout.backend.dto

import chloe.sprout.backend.domain.User
import java.util.UUID

data class UserSignupRequest(
    val email: String,
    val password: String,
    val name: String
)

data class UserSignupResponse(
    val id: UUID,
    val email: String,
    val name: String
) {
    companion object {
        fun from(user: User): UserSignupResponse {
            return UserSignupResponse(
                id = user.id,
                email = user.email,
                name = user.name
            )
        }
    }
}
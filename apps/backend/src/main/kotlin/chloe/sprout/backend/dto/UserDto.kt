package chloe.sprout.backend.dto

import chloe.sprout.backend.domain.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

data class UserSignupRequest(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
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

data class UserLoginRequest(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val password: String
)

data class UserLoginResponse(
    val id: UUID,
    val email: String,
    val name: String
) {
    companion object {
        fun from(user: User): UserLoginResponse {
            return UserLoginResponse(
                id = user.id,
                email = user.email,
                name = user.name
            )
        }
    }
}
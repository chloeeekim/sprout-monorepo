package chloe.sprout.backend.property

import org.springframework.stereotype.Component

@Component
class SecurityAllowlistProperties {
    val allowlist = listOf(
        "/api/users/login",
        "/api/users/signup",
        "/api/users/refresh"
    )
}
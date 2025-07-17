package chloe.sprout.backend.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    val secret: String,
    val accessExpiration: Long,
    val refreshExpiration: Long
)
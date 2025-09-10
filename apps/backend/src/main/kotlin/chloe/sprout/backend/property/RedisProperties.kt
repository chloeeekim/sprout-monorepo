package chloe.sprout.backend.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.data.redis")
class RedisProperties(
    val host: String,
    val port: Int,
    val ssl: Ssl = Ssl()
) {
    class Ssl(
        val enabled: Boolean = false
    )
}
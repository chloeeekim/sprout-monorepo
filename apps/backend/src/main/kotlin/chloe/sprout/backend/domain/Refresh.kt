package chloe.sprout.backend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash(value = "refresh", timeToLive = 60 * 60 * 24 * 2) // 2주
class Refresh(
    @Id
    val email: String,

    @Indexed
    var refreshToken: String
)
package chloe.sprout.backend.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RefreshService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun saveRefreshToken(email: String, token: String) {
        redisTemplate.opsForValue().set("refresh:$email", token, Duration.ofDays(14))
    }

    fun getRefreshToken(email: String): String? {
        return redisTemplate.opsForValue().get("refresh:$email")
    }

    fun deleteRefreshToken(email: String) {
        redisTemplate.delete("refresh:$email")
    }
}
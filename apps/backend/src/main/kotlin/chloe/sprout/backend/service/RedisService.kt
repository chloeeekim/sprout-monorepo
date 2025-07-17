package chloe.sprout.backend.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService(
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

    fun saveDenylist(token: String, minutes: Duration) {
        redisTemplate.opsForValue().set("denylist:$token", "logout", minutes)
    }

    fun getDenylist(token: String): String? {
        return redisTemplate.opsForValue().get("denylist:$token")
    }

    fun deleteDenylist(token: String) {
        redisTemplate.delete("denylist:$token")
    }
}
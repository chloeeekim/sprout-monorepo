package chloe.sprout.backend.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class SseService {
    private val logger = LoggerFactory.getLogger(javaClass)

    // 사용자 ID와 SseEmitter를 1:1로 매핑하여 저장
    private val emitters: ConcurrentHashMap<UUID, SseEmitter> = ConcurrentHashMap()

    fun subscribe(userId: UUID): SseEmitter {
        // 1시간 타임아웃 설정
        val emitter = SseEmitter(3600_000L)
        emitters[userId] = emitter

        logger.info("New SSE connection for user: $userId")

        // 연결이 완료되거나 타임아웃되면 emitters 맵에서 제거
        emitter.onCompletion {
            emitters.remove(userId)
            logger.info("SSE connection completed for user: $userId")
        }
        emitter.onTimeout {
            emitters.remove(userId)
            logger.info("SSE connection timed out for user: $userId")
        }
        emitter.onError {
            emitters.remove(userId)
            logger.error("SSE connection error for user: $userId", it)
        }

        // 연결 직후, 연결이 성공적으로 수립되었음을 알리는 더미 이벤트 전송
        send(userId, "sse-connection-established", "Connection established for user $userId")

        return emitter
    }

    // 특정 사용자에게 이벤트 전송
    fun send(userId: UUID, eventName: String, data: Any) {
        emitters[userId]?.let {
            try {
                it.send(SseEmitter.event().name(eventName).data(data))
            } catch (e: Exception) {
                logger.error("Failed to send SSE event to user: $userId", e)
            }
        }
    }
}
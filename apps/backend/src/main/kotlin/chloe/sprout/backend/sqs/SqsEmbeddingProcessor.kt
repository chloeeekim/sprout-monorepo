package chloe.sprout.backend.sqs

import chloe.sprout.backend.dto.EmbeddingCreateRequest
import chloe.sprout.backend.openai.OpenAiService
import chloe.sprout.backend.service.NoteEmbeddingService
import chloe.sprout.backend.service.NoteLinkService
import chloe.sprout.backend.service.SseService
import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@Profile("sqs")
class SqsEmbeddingProcessor(
    private val noteEmbeddingService: NoteEmbeddingService,
    private val noteLinkService: NoteLinkService,
    private val openAiService: OpenAiService,
    private val sseService: SseService,
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val queueName = "note-updated-queue.fifo"
        private const val LOCK_KEY_PREFIX = "embedding_lock:"
        private val LOCK_DURATION = Duration.ofMinutes(5)
    }

    @SqsListener(queueName)
    fun handleNoteUpdated(message: String) {
        // 메시지를 DTO로 변환
        val data = objectMapper.readValue(message, EmbeddingCreateRequest::class.java)
        val noteId = data.noteId
        val userId = data.userId

        // 잦은 임베딩 생성 요청 방지 lock
        val lockKey = "$LOCK_KEY_PREFIX$noteId"
        val isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", LOCK_DURATION)

        if (isLocked != true) {
            log.info("Note ID $noteId is already being processed. Skipping.")
            return
        }

        try {
            log.info("Processing note ID: $noteId for embedding update.")

            // 임베딩할 콘텐츠 가져오기 - 없는 경우 재시도 할 수 있도록 lock 해제 후 종료
            val contentToEmbed = noteEmbeddingService.getContentForEmbedding(userId, noteId)
                ?: run {
                    redisTemplate.delete(lockKey)
                    return
                }

            // OpenAI API를 통해 임베딩 생성
            val embeddingVector = openAiService.createEmbedding(contentToEmbed)

            // 임베딩 저장
            if (embeddingVector != null) {
                noteEmbeddingService.saveEmbedding(userId, noteId, embeddingVector)
                log.info("Successfully generated and saved embedding for note ID $noteId")

                // 유사 노트 링크 업데이트
                noteLinkService.updateLinksForNote(noteId, userId)
                log.info("Successfully updated note links for note ID $noteId")

                // 임베딩 저장 완료 알림 전송
                val notification = mapOf("noteId" to noteId.toString())
                sseService.send(userId, "embedding-updated", notification)
                log.info("Sent embedding-updated notification for note ID $noteId to user $userId")
            } else {
                log.warn("Failed to generate embedding for note ID $noteId. Vector was null.")
            }
        } catch (e: Exception) {
            log.error("Error processing embedding for note ID: $noteId", e)

            // 오류 발생 시 재시도 할 수 있도록 lock 해제
            redisTemplate.delete(lockKey)
        }
    }
}
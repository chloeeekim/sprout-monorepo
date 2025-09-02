package chloe.sprout.backend.kafka

import chloe.sprout.backend.domain.NoteEmbedding
import chloe.sprout.backend.openai.OpenAiService
import chloe.sprout.backend.repository.NoteEmbeddingRepository
import chloe.sprout.backend.repository.NoteRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.UUID

@Service
class EmbeddingConsumer (
    private val noteRepository: NoteRepository,
    private val noteEmbeddingRepository: NoteEmbeddingRepository,
    private val openAiService: OpenAiService,
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val NOTE_UPDATED_TOPIC = "note.updated"
        private const val EMBEDDING_GROUP_ID = "embedding_group"
        private const val LOCK_KEY_PREFIX = "embedding_lock:"
        private val LOCK_DURATION = Duration.ofMinutes(5)
    }

    @Transactional
    @KafkaListener(topics = [NOTE_UPDATED_TOPIC], groupId = EMBEDDING_GROUP_ID)
    fun handleNoteUpdate(noteId: UUID) {
        // 잦은 임베딩 생성 요청을 막기 위해 특정 시간(5분) 동안 lock
        val lockKey = "$LOCK_KEY_PREFIX$noteId"
        val isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", LOCK_DURATION)

        if (isLocked != true) {
            log.info("Note ID $noteId is already being processed. Skipping.")
            return
        }

        try {
            log.info("Processing note ID: $noteId for embedding update.")

            // Note 확인
            val note = noteRepository.findByIdOrNull(noteId)
            if (note == null) {
                log.warn("Note ID $noteId not found. Cannot generate embedding.")
                return
            }

            // 내용이 없는 경우, 임베딩 생성 안 함
            if (note.content.isNullOrBlank()) return

            // 임베딩할 콘텐츠 생성
            val contentToEmbed = "Title: ${note.title}\n\nContent: ${note.content}"

            // OpenAI API를 통해 임베딩 생성
            val embeddingVector = openAiService.createEmbedding(contentToEmbed)

            // 기존 임베딩이 있는지 확인 후, 없으면 새로 생성하고 있으면 업데이트
            val noteEmbedding = noteEmbeddingRepository.findByIdOrNull(noteId)
                ?.apply {
                    embedding = embeddingVector
                }
                ?: NoteEmbedding(
                    id = noteId,
                    note = note,
                    embedding = embeddingVector
                )

            noteEmbeddingRepository.save(noteEmbedding)
            log.info("Successfully generated and saved embedding for note ID $noteId")
        } catch (e: Exception) {
            log.error("Error processing embedding for note ID: $noteId", e)
        }
    }
}
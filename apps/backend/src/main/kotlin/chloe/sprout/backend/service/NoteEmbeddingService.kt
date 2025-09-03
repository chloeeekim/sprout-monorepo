package chloe.sprout.backend.service

import chloe.sprout.backend.repository.NoteEmbeddingRepository
import chloe.sprout.backend.repository.NoteRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class NoteEmbeddingService(
    private val noteRepository: NoteRepository,
    private val noteEmbeddingRepository: NoteEmbeddingRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun getContentForEmbedding(noteId: UUID): String? {
        // Note 확인
        val note = noteRepository.findByIdOrNull(noteId)
            ?: run {
                logger.warn("Note not found for id $noteId. Cannot generate embedding.")
                return null
            }

        // content가 없는 경우 임베딩 생성 안 함
        if (note.content.isNullOrBlank()) {
            logger.info("Note ID $noteId has no content. Skipping embedding generation.")
            return null
        }

        // 임베딩할 콘텐츠 리턴
        return "Title: ${note.title}\n\nContent: ${note.content}"
    }

    @Transactional
    fun saveEmbedding(noteId: UUID, embeddingVector: FloatArray) {
        try {
            // 임베딩 upsert
            noteEmbeddingRepository.upsertEmbedding(noteId, embeddingVector)
            logger.info("Successfully saved embedding for note ID $noteId")
        } catch (e: Exception) {
            logger.error("Error saving embedding for note ID: $noteId", e)
        }
    }
}
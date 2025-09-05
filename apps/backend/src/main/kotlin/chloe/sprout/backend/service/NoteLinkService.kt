package chloe.sprout.backend.service

import chloe.sprout.backend.domain.NoteLink
import chloe.sprout.backend.dto.NoteLinkResponse
import chloe.sprout.backend.exception.note.NoteNotFoundException
import chloe.sprout.backend.exception.note.NoteOwnerMismatchException
import chloe.sprout.backend.repository.NoteEmbeddingRepository
import chloe.sprout.backend.repository.NoteLinkRepository
import chloe.sprout.backend.repository.NoteRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class NoteLinkService(
    private val noteRepository: NoteRepository,
    private val noteEmbeddingRepository: NoteEmbeddingRepository,
    private val noteLinkRepository: NoteLinkRepository
) {
    companion object {
        private const val SIMILARITY_THRESHOLD = 0.5
        private const val SIMILAR_NOTE_LIMIT = 3
    }

    @Transactional
    fun updateLinksForNote(noteId: UUID, userId: UUID) {
        // 기존 연결 삭제
        noteLinkRepository.deleteAllBySourceNoteIdOrTargetNoteId(noteId, noteId)

        // Note 확인
        val sourceNote = noteRepository.findByIdOrNull(noteId)
            ?: throw NoteNotFoundException()

        // owner 일치 여부 확인
        if (sourceNote.owner.id != userId) {
            throw NoteOwnerMismatchException()
        }

        // Note Embedding 확인 - 없으면 Link 생성 안 함
        val sourceEmbedding = noteEmbeddingRepository.findByIdOrNull(noteId)
            ?: return

        sourceEmbedding.embedding?.let {
            // 유사 노트 검색
            val similar = noteEmbeddingRepository.findSimilarEmbeddings(userId, noteId, it, SIMILAR_NOTE_LIMIT, SIMILARITY_THRESHOLD)

            // NoteLink 생성
            val newLinks = similar.map { targetNoteEmbedding ->
                val targetNote = targetNoteEmbedding.note
                NoteLink(sourceNote.owner, sourceNote, targetNote)
            }

            // DB에 저장
            noteLinkRepository.saveAll(newLinks)
        }
    }

    @Transactional(readOnly = true)
    fun findAllLinks(userId: UUID): List<NoteLinkResponse> {
        // 모든 NoteLink 조회
        val links = noteLinkRepository.findAllByOwnerId(userId)

        // response DTO로 변환 후 반환
        return links.map { NoteLinkResponse.from(it) }
    }
}
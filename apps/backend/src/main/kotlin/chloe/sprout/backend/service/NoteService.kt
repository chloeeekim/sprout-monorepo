package chloe.sprout.backend.service

import chloe.sprout.backend.domain.Note
import chloe.sprout.backend.domain.NoteTag
import chloe.sprout.backend.dto.*
import chloe.sprout.backend.exception.folder.FolderNotFoundException
import chloe.sprout.backend.exception.note.NoteNotFoundException
import chloe.sprout.backend.exception.note.NoteOwnerMismatchException
import chloe.sprout.backend.exception.note.NoteTitleRequiredException
import chloe.sprout.backend.exception.tag.TagNotFoundException
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.repository.FolderRepository
import chloe.sprout.backend.repository.NoteEmbeddingRepository
import chloe.sprout.backend.repository.NoteRepository
import chloe.sprout.backend.repository.TagRepository
import chloe.sprout.backend.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository,
    private val tagRepository: TagRepository,
    private val folderRepository: FolderRepository,
    private val kafkaTemplate: KafkaTemplate<String, UUID>,
    private val noteEmbeddingRepository: NoteEmbeddingRepository
) {
    companion object {
        private const val NOTE_UPDATED_TOPIC = "note.updated"
    }

    @Transactional
    fun createNote(userId: UUID, request: NoteCreateRequest): NoteCreateResponse {
        // User 확인
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        // title blank 여부 확인
        if (request.title.isBlank()) {
            throw NoteTitleRequiredException()
        }

        // Folder 확인
        val folder = request.folderId?.let {
            folderRepository.findByIdOrNull(it)
                ?: throw FolderNotFoundException()
        }

        // Note entity 생성
        val note = Note(
            title = request.title,
            content = request.content,
            isFavorite = false, // false로 초기화
            owner = user,
            folder = folder
        )

        // Note에 Tag 업데이트
        updateTags(note, request.tags)

        // DB 저장
        val save = noteRepository.save(note)

        // response DTO로 변환 후 반환
        return NoteCreateResponse.from(save)
    }

    @Transactional
    fun copyNote(noteId: UUID, userId: UUID): NoteCreateResponse {
        // Note 확인
        val note = noteRepository.findByIdOrNull(noteId)
            ?: throw NoteNotFoundException()

        // owner 일치 여부 확인
        if (note.owner.id != userId) {
            throw NoteOwnerMismatchException()
        }

        // Title 변경
        val newTitle = note.title + " (1)";

        // Note entity 생성
        val newNote = Note(
            title = newTitle,
            content = note.content,
            isFavorite = false,
            owner = note.owner,
            folder = note.folder
        )

        // Note에 Tag 업데이트
        val tagIds = note.noteTags.map { it.tag.id }
        updateTags(newNote, tagIds);

        // DB 저장
        val save = noteRepository.save(newNote)

        // Kafka 이벤트 발행
        save.id.let {
            kafkaTemplate.send(NOTE_UPDATED_TOPIC, userId.toString(), it)
        }

        // response DTO로 변환 후 반환
        return NoteCreateResponse.from(save)
    }

    @Transactional(readOnly = true)
    fun getNoteById(noteId: UUID, userId: UUID): NoteDetailResponse {
        // Note 확인
        val note = noteRepository.findByIdOrNull(noteId)
            ?: throw NoteNotFoundException()

        // owner 일치 여부 확인
        if (note.owner.id != userId) {
            throw NoteOwnerMismatchException()
        }

        // response DTO로 변환 후 반환
        return NoteDetailResponse.from(note)
    }

    @Transactional(readOnly = true)
    fun getRandomNoteByUserId(userId: UUID): NoteDetailResponse {
        // 랜덤 노트 탐색
        val note = noteRepository.findRandomNoteByOwnerId(userId)
            ?: throw NoteNotFoundException()

        // response DTO로 변환 후 반환
        return NoteDetailResponse.from(note)
    }

    @Transactional(readOnly = true)
    fun getAllNotesByUserId(userId: UUID, lastUpdatedAt: OffsetDateTime? = null, lastId: UUID? = null, tagId: UUID? = null, keyword: String? = null, folderId: UUID? = null, pageable: Pageable): Slice<NoteListResponse> {
        // Folder 존재 여부 확인
        folderId?.let {
            folderRepository.findByIdOrNull(it)
                ?: throw FolderNotFoundException()
        }

        // Tag 존재 여부 확인
        tagId?.let {
            tagRepository.findByIdOrNull(it)
                ?: throw TagNotFoundException()
        }

        // tag, keyword 등 컨디션에 따라 Note 목록을 Slice 형태로 조회
        val notes = noteRepository.findNotesByOwnerId(userId, lastUpdatedAt, lastId, tagId, keyword, folderId, pageable)

        // Note 목록을 response DTO로 변환 후 응답
        return notes.map { NoteListResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun findSimilarNotes(noteId: UUID, userId: UUID): List<NoteListResponse> {
        // Note 확인
        val originNote = noteRepository.findByIdOrNull(noteId)
            ?: throw NoteNotFoundException()

        // owner 일치 여부 확인
        if (originNote.owner.id != userId) {
            throw NoteOwnerMismatchException()
        }

        // 노트의 임베딩 조회 - 임베딩이 없으면 빈 리스트 반환
        val originEmbedding = noteEmbeddingRepository.findByIdOrNull(noteId)
            ?: return emptyList()
        originEmbedding.embedding ?: return emptyList()

        // 유사 임베딩 검색
        val similarEmbeddings = noteEmbeddingRepository.findSimilarEmbeddings(noteId, originEmbedding.embedding!!, 3)

        // response DTO로 변환 후 응답
        return similarEmbeddings.map { NoteListResponse.from(it.note) }
    }

    @Transactional
    fun updateNote(userId: UUID, noteId: UUID, request: NoteUpdateRequest): NoteUpdateResponse {
        // Note 확인
        val note = noteRepository.findByIdOrNull(noteId)
            ?: throw NoteNotFoundException()

        // owner 일치 여부 확인
        if (note.owner.id != userId) {
            throw NoteOwnerMismatchException()
        }

        // title 업데이트
        if (request.title.isPresent) {
            val newTitle = request.title.orElse("").trim()
            if (newTitle.isBlank()) {
                throw NoteTitleRequiredException()
            }
            note.title = newTitle
        }

        // content 업데이트
        if (request.content.isPresent) {
            note.content = request.content.orElse(null)
        }

        // folder 업데이트
        if (request.folderId.isPresent) {
            note.folder = request.folderId.orElse(null)?.let {
                folderRepository.findByIdOrNull(it)
                    ?: throw FolderNotFoundException()
            }
        }

        // tags 업데이트
        if (request.tags.isPresent) {
            updateTags(note, request.tags.orElse(emptyList()))
        }

        // timestamp 업데이트
        note.touch()

        // 업데이트된 노트 저장
        val save = noteRepository.save(note)

        // Kafka 이벤트 발행
        save.id.let {
            kafkaTemplate.send(NOTE_UPDATED_TOPIC, userId.toString(), it)
        }

        // response DTO로 변환 후 반환
        return NoteUpdateResponse.from(save)
    }

    @Transactional
    fun toggleIsFavorite(userId: UUID, noteId: UUID): NoteUpdateResponse {
        // Note 확인
        val note = noteRepository.findByIdOrNull(noteId)
            ?: throw NoteNotFoundException()

        // owner 일치 여부 확인
        if (note.owner.id != userId) {
            throw NoteOwnerMismatchException()
        }

        // isFavorite toggle
        note.isFavorite = !note.isFavorite

        val save = noteRepository.save(note)
        return NoteUpdateResponse.from(save)
    }

    @Transactional
    fun deleteNote(userId: UUID, noteId: UUID) {
        // Note 확인
        val note = noteRepository.findByIdOrNull(noteId)
            ?: throw NoteNotFoundException()

        // owner 일치 여부 확인
        if (note.owner.id != userId) {
            throw NoteOwnerMismatchException()
        }

        // Note 삭제
        noteRepository.delete(note)
    }

    private fun updateTags(note: Note, tagIds: List<UUID>) {
        val currentTagIds = note.noteTags.map { it.tag.id!! }.toSet()
        val newTagIds = tagIds.toSet()

        // 추가해야 할 태그 ID
        val toAdd = newTagIds - currentTagIds

        // 제거해야 할 태그 ID
        val toRemove = currentTagIds - newTagIds

        // 기존 태그 제거
        note.noteTags.removeIf { it.tag.id in toRemove }

        // 새 태그 추가
        if (toAdd.isNotEmpty()) {
            val tagsToAdd = tagRepository.findAllById(toAdd)
            tagsToAdd.forEach { tag ->
                note.noteTags.add(NoteTag(note, tag))
            }
        }
    }
}
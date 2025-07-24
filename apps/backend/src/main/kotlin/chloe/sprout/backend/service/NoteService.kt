package chloe.sprout.backend.service

import chloe.sprout.backend.domain.Note
import chloe.sprout.backend.domain.NoteTag
import chloe.sprout.backend.domain.Tag
import chloe.sprout.backend.domain.User
import chloe.sprout.backend.dto.*
import chloe.sprout.backend.exception.note.NoteNotFoundException
import chloe.sprout.backend.exception.note.NoteOwnerMismatchException
import chloe.sprout.backend.exception.note.NoteTitleRequiredException
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.repository.NoteRepository
import chloe.sprout.backend.repository.TagRepository
import chloe.sprout.backend.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository,
    private val tagRepository: TagRepository
) {
    @Transactional
    fun createNote(userId: UUID, request: NoteCreateRequest): NoteCreateResponse {
        // User 확인
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        // title blank 여부 확인
        if (request.title.isBlank()) {
            throw NoteTitleRequiredException()
        }

        // Note entity 생성
        val note = Note(
            title = request.title,
            content = request.content,
            isFavorite = false, // false로 초기화
            owner = user
        )

        // Note에 Tag 업데이트
        updateTags(note, request.tags, user)

        // DB 저장
        val save = noteRepository.save(note)

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
    fun getAllNotesByUserId(userId: UUID, tag: String?, keyword: String?): List<NoteListResponse> {
        val notes = when {
            !tag.isNullOrBlank() -> {
                // tag 파라미터가 있으면 해당 태그를 가진 노트만 반환
                noteRepository.findAllByOwnerIdAndTagName(userId, tag)
            }
            !keyword.isNullOrBlank() -> {
                // keyword 파라미터가 있으면 해당 키워드를 가진 노트만 반환
                noteRepository.searchByOwnerIdAndKeyword(userId, keyword)
            }
            else -> {
                // tag, keyword 파라미터가 없으면 모든 노트 반환
                noteRepository.findAllByOwnerId(userId)
            }
        }

        // Note 목록을 response DTO로 변환 후 응답
        return notes.map { NoteListResponse.from(it) }
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

        // title blank 여부 확인
        if (request.title.isBlank()) {
            throw NoteTitleRequiredException()
        }

        // note 내용 업데이트
        val save = note.run {
            title = request.title
            content = request.content
            noteRepository.save(this)
        }
        updateTags(note, request.tags, note.owner)

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

    private fun updateTags(note: Note, tagName: List<String>, owner: User) {
        note.noteTags.clear()
        tagName.forEach { t ->
            val tag = tagRepository.findByNameAndOwner(t, owner) ?: Tag(name = t, owner = owner)
            note.noteTags.add(NoteTag(note, tag))
        }
    }
}
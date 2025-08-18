package chloe.sprout.backend.service

import chloe.sprout.backend.domain.Note
import chloe.sprout.backend.domain.NoteTag
import chloe.sprout.backend.domain.Tag
import chloe.sprout.backend.domain.User
import chloe.sprout.backend.dto.*
import chloe.sprout.backend.exception.folder.FolderNotFoundException
import chloe.sprout.backend.exception.note.NoteNotFoundException
import chloe.sprout.backend.exception.note.NoteOwnerMismatchException
import chloe.sprout.backend.exception.note.NoteTitleRequiredException
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.repository.FolderRepository
import chloe.sprout.backend.repository.NoteRepository
import chloe.sprout.backend.repository.TagRepository
import chloe.sprout.backend.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository,
    private val tagRepository: TagRepository,
    private val folderRepository: FolderRepository
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
    fun getAllNotesByUserId(userId: UUID, lastUpdatedAt: OffsetDateTime? = null, lastId: UUID? = null, tag: String? = null, keyword: String? = null, folderId: UUID? = null, pageable: Pageable): Slice<NoteListResponse> {
        // tag, keyword 등 컨디션에 따라 Note 목록을 Slice 형태로 조회
        val notes = noteRepository.findNotesByOwnerId(userId, lastUpdatedAt, lastId, tag, keyword, folderId, pageable)

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

        // Folder 확인
        val folder = request.folderId?.let {
            folderRepository.findByIdOrNull(it)
                ?: throw FolderNotFoundException()
        }

        // note 내용 업데이트
        val save = note.run {
            title = request.title
            content = request.content
            updateTags(this, request.tags, this.owner)
            this.folder = folder
            this.touch()
            noteRepository.save(this)
        }

        tagRepository.deleteUnusedTagsByOwnerId(userId);

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
package chloe.sprout.backend.dto

import chloe.sprout.backend.domain.Note
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.*

data class NoteCreateRequest(
    @field:NotBlank
    val title: String,

    val content: String?,
    val tags: List<String> = emptyList()
)

data class NoteCreateResponse(
    val id: UUID,
    val title: String,
    val content: String?,
    val isFavorite: Boolean,
    val tags: List<String>,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(note: Note): NoteCreateResponse {
            return NoteCreateResponse(
                id = note.id,
                title = note.title,
                content = note.content,
                isFavorite = note.isFavorite,
                tags = note.noteTags.map { it.tag.name },
                createdAt = requireNotNull(note.createdAt)
            )
        }
    }
}

data class NoteUpdateRequest(
    @field:NotBlank
    val title: String,

    val content: String?,
    val tags: List<String>
)

data class NoteUpdateResponse(
    val id: UUID,
    val title: String,
    val content: String?,
    val isFavorite: Boolean,
    val tags: List<String>,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(note: Note): NoteUpdateResponse {
            return NoteUpdateResponse(
                id = note.id,
                title = note.title,
                content = note.content,
                isFavorite = note.isFavorite,
                tags = note.noteTags.map { it.tag.name },
                updatedAt = requireNotNull(note.updatedAt)
            )
        }
    }
}

data class NoteDetailResponse(
    val id: UUID,
    val title: String,
    val content: String?,
    val isFavorite: Boolean,
    val tags: List<String>,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(note: Note): NoteDetailResponse {
            return NoteDetailResponse(
                id = note.id,
                title = note.title,
                content = note.content,
                isFavorite = note.isFavorite,
                tags = note.noteTags.map { it.tag.name },
                updatedAt = requireNotNull(note.updatedAt)
            )
        }
    }
}

data class NoteListResponse(
    val id: UUID,
    val title: String,
    val content: String?,
    val isFavorite: Boolean,
    val tags: List<String>,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(note: Note): NoteListResponse {
            return NoteListResponse(
                id = note.id,
                title = note.title,
                content = note.content,
                isFavorite = note.isFavorite,
                tags = note.noteTags.map { it.tag.name },
                updatedAt = requireNotNull(note.updatedAt)
            )
        }
    }
}
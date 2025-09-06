package chloe.sprout.backend.dto

import chloe.sprout.backend.domain.Note
import jakarta.validation.constraints.NotBlank
import org.openapitools.jackson.nullable.JsonNullable
import java.time.OffsetDateTime
import java.util.*

private const val TRUNCATED_LENGTH = 100

data class NoteCreateRequest(
    @field:NotBlank
    val title: String,

    val content: String?,
    val tags: List<UUID> = emptyList(),
    val folderId: UUID? = null
)

data class NoteCreateResponse(
    val id: UUID,
    val title: String,
    val content: String?,
    val isFavorite: Boolean,
    val tags: List<TagDetailResponse>,
    val folderId: UUID?,
    val updatedAt: OffsetDateTime
) {
    companion object {
        fun from(note: Note): NoteCreateResponse {
            return NoteCreateResponse(
                id = note.id,
                title = note.title,
                content = note.content,
                isFavorite = note.isFavorite,
                tags = note.noteTags.map { TagDetailResponse.from(it.tag) },
                folderId = note.folder?.id,
                updatedAt = requireNotNull(note.updatedAt)
            )
        }
    }
}

data class NoteUpdateRequest(
    val title: JsonNullable<String> = JsonNullable.undefined(),
    val content: JsonNullable<String?> = JsonNullable.undefined(),
    val tags: JsonNullable<List<UUID>> = JsonNullable.undefined(),
    val folderId: JsonNullable<UUID?> = JsonNullable.undefined()
)

data class NoteUpdateResponse(
    val id: UUID,
    val title: String,
    val content: String?,
    val isFavorite: Boolean,
    val tags: List<TagDetailResponse>,
    val folderId: UUID?,
    val updatedAt: OffsetDateTime
) {
    companion object {
        fun from(note: Note): NoteUpdateResponse {
            return NoteUpdateResponse(
                id = note.id,
                title = note.title,
                content = note.content,
                isFavorite = note.isFavorite,
                tags = note.noteTags.map { TagDetailResponse.from(it.tag) },
                folderId = note.folder?.id,
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
    val tags: List<TagDetailResponse>,
    val folderId: UUID?,
    val updatedAt: OffsetDateTime
) {
    companion object {
        fun from(note: Note): NoteDetailResponse {
            return NoteDetailResponse(
                id = note.id,
                title = note.title,
                content = note.content,
                isFavorite = note.isFavorite,
                tags = note.noteTags.map { TagDetailResponse.from(it.tag) },
                folderId = note.folder?.id,
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
    val tags: List<TagDetailResponse>,
    val folderId: UUID?,
    val updatedAt: OffsetDateTime
) {
    companion object {
        fun from(note: Note): NoteListResponse {
            return NoteListResponse(
                id = note.id,
                title = note.title,
                content = note.content,
                isFavorite = note.isFavorite,
                tags = note.noteTags.map { TagDetailResponse.from(it.tag) },
                folderId = note.folder?.id,
                updatedAt = requireNotNull(note.updatedAt)
            )
        }
    }
}

data class NoteSimpleResponse(
    val id: UUID,
    val title: String,
    val content: String?
) {
    companion object {
        fun from(note: Note): NoteSimpleResponse {
            return NoteSimpleResponse(
                id = note.id,
                title = note.title,
                content = note.content?.let {
                    if (it.length > TRUNCATED_LENGTH) {
                        it.substring(0, TRUNCATED_LENGTH)
                    } else {
                        it
                    }
                } ?: ""
            )
        }
    }
}
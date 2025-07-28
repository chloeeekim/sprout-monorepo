package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.Note
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.UUID

interface NoteRepositoryCustom {
    fun findNotesByOwnerId(
        userId: UUID,
        lastUpdatedAt: OffsetDateTime?,
        lastId: UUID?,
        tag: String?,
        keyword: String?,
        pageable: Pageable
    ): Slice<Note>
}

package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.Note
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.time.OffsetDateTime
import java.util.*

interface NoteRepositoryCustom {
    fun findNotesByOwnerId(
        userId: UUID,
        lastUpdatedAt: OffsetDateTime?,
        lastId: UUID?,
        tagId: UUID?,
        keyword: String?,
        folderId: UUID?,
        pageable: Pageable
    ): Slice<Note>
}

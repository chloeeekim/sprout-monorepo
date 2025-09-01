package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.Note
import chloe.sprout.backend.repository.custom.NoteRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
interface NoteRepository : JpaRepository<Note, UUID>, NoteRepositoryCustom {

    @Query(value = """
        SELECT * FROM notes WHERE owner_id = :owner
        ORDER BY LN(EXTRACT(DAY FROM (NOW() - updated_at)) + 1) * RANDOM() DESC
        LIMIT 1""",
        nativeQuery = true)
    fun findRandomNoteByOwnerId(owner: UUID): Note?
}
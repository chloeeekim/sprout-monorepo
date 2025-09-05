package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.NoteLink
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NoteLinkRepository : JpaRepository<NoteLink, UUID> {
    @Modifying
    fun deleteAllBySourceNoteIdOrTargetNoteId(sourceNoteId: UUID, targetNoteId: UUID)

    fun findAllByOwnerId(ownerId: UUID): List<NoteLink>
}
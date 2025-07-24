package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.Note
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NoteRepository : JpaRepository<Note, UUID> {
    fun findAllByOwnerId(userId: UUID): List<Note>

    @Query("""SELECT n FROM Note n JOIN n.noteTags nt JOIN nt.tag t
        WHERE n.owner.id = :userId AND t.name = :tagName""")
    fun findAllByOwnerIdAndTagName(userId: UUID, tagName: String): List<Note>
}
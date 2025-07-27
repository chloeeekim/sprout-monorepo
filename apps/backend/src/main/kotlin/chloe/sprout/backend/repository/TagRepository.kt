package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.Tag
import chloe.sprout.backend.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TagRepository : JpaRepository<Tag, UUID> {
    fun findByNameAndOwner(name: String, owner: User): Tag?

    fun findAllByOwnerId(ownerId: UUID): List<Tag>

    @Query("""
        DELETE FROM Tag t
        WHERE t.owner.id = :ownerId
        AND NOT EXISTS (
            SELECT 1 FROM NoteTag nt
            WHERE nt.tag = t
        )
    """)
    @Modifying
    fun deleteUnusedTagsByOwnerId(ownerId: UUID)
}
package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.NoteEmbedding
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NoteEmbeddingRepository : JpaRepository<NoteEmbedding, UUID> {
}
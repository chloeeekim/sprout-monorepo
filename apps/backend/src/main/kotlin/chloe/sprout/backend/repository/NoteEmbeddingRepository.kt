package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.NoteEmbedding
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NoteEmbeddingRepository : JpaRepository<NoteEmbedding, UUID> {
    @Query(value = """
        SELECT * FROM note_embeddings
        WHERE note_id != :noteId
        ORDER BY embedding <=> CAST(:embedding AS vector)
        LIMIT :limit
    """, nativeQuery = true)
    fun findSimilarEmbeddings(noteId: UUID, embedding: FloatArray, limit: Int): List<NoteEmbedding>

    @Modifying
    @Query(value = """
        INSERT INTO note_embeddings (note_id, embedding, created_at, updated_at)
        VALUES (:noteId, CAST(:embedding AS vector), NOW(), NOW())
        ON CONFLICT (note_id) DO UPDATE SET
        embedding = CAST(:embedding AS vector),
        updated_at = NOW()
    """, nativeQuery = true)
    fun upsertEmbedding(noteId: UUID, embedding: FloatArray)
}
package chloe.sprout.backend.dto

import java.util.UUID

data class EmbeddingCreateRequest(
    val userId: UUID,
    val noteId: UUID
)
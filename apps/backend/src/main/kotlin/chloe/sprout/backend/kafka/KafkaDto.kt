package chloe.sprout.backend.kafka

import java.util.UUID

data class EmbeddingCreateRequest(
    val userId: UUID,
    val noteId: UUID
)
package chloe.sprout.backend.service.event

import java.util.UUID

interface EventPublisher {
    fun publish(userId: UUID, noteId: UUID)
}
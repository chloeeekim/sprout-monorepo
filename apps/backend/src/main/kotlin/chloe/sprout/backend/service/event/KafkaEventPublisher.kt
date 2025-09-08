package chloe.sprout.backend.service.event

import chloe.sprout.backend.dto.EmbeddingCreateRequest
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Profile("kafka")
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : EventPublisher {
    companion object {
        private const val NOTE_UPDATED_TOPIC = "note.updated"
    }

    override fun publish(userId: UUID, noteId: UUID) {
        kafkaTemplate.send(NOTE_UPDATED_TOPIC, userId.toString(), EmbeddingCreateRequest(userId = userId, noteId = noteId))
    }
}
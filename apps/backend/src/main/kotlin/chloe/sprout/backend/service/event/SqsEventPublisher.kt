package chloe.sprout.backend.service.event

import chloe.sprout.backend.dto.EmbeddingCreateRequest
import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.context.annotation.Profile
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Profile("sqs")
class SqsEventPublisher(
    private val sqsTemplate: SqsTemplate,
    private val objectMapper: ObjectMapper
) : EventPublisher {
    companion object {
        private const val queueName = "note-updated-queue.fifo"
    }

    override fun publish(userId: UUID, noteId: UUID) {
        val payload = objectMapper.writeValueAsString(EmbeddingCreateRequest(userId, noteId))
        sqsTemplate.send(queueName,
            MessageBuilder.withPayload(payload)
                .setHeader("MessageGroupId", userId.toString())
                .build()
        )
    }
}
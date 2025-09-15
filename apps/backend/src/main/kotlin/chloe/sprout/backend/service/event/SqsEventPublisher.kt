package chloe.sprout.backend.service.event

import chloe.sprout.backend.dto.EmbeddingCreateRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.util.UUID

@Component
@Profile("sqs")
class SqsEventPublisher(
    private val sqsAsyncClient: SqsAsyncClient,
    private val objectMapper: ObjectMapper
) : EventPublisher {
    companion object {
        private const val queueUrl = "https://sqs.ap-northeast-2.amazonaws.com/540147314949/note-updated-queue.fifo"
    }

    override fun publish(userId: UUID, noteId: UUID) {
        val payload = objectMapper.writeValueAsString(EmbeddingCreateRequest(userId, noteId))

        val request = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(payload)
            .messageGroupId(userId.toString())
            .build()

        sqsAsyncClient.sendMessage(request)
    }
}
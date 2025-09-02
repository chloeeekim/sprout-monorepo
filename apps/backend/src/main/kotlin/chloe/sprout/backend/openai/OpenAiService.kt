package chloe.sprout.backend.openai

import chloe.sprout.backend.dto.OpenAiEmbeddingRequest
import chloe.sprout.backend.dto.OpenAiEmbeddingResponse
import chloe.sprout.backend.property.OpenAiProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class OpenAiService(
    @Qualifier("openAiRestTemplate")
    private val restTemplate: RestTemplate,
    private val openAiProperties: OpenAiProperties
) {
    private val embeddingUrl = "https://api.openai.com/v1/embeddings"

    fun createEmbedding(text: String): FloatArray? {
        val request = OpenAiEmbeddingRequest(model = openAiProperties.model, input = text)

        val response = restTemplate.postForObject(embeddingUrl, request, OpenAiEmbeddingResponse::class.java)

        return response?.data?.firstOrNull()?.embedding
    }
}
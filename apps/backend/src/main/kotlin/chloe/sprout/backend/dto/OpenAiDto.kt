package chloe.sprout.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenAiEmbeddingRequest(
    val model: String,
    val input: String
)

data class OpenAiEmbeddingResponse(
    @JsonProperty("object")
    val obj: String,
    val data: List<EmbeddingData>,
    val model: String,
    val usage: UsageData
) {
    data class EmbeddingData(
        @JsonProperty("object")
        val obj: String,
        val embedding: FloatArray,
        val index: Int
    )

    data class UsageData(
        @JsonProperty("prompt_tokens")
        val promptTokens: Int,
        @JsonProperty("total_tokens")
        val totalTokens: Int
    )
}
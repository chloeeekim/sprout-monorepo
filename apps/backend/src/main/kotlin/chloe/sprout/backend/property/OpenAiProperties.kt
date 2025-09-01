package chloe.sprout.backend.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "openai")
class OpenAiProperties(
    val secretKey: String,
    val model: String
)
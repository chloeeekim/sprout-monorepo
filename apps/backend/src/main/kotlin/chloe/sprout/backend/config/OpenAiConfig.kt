package chloe.sprout.backend.config

import chloe.sprout.backend.property.OpenAiProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class OpenAiConfig(
    private val openAiProperties: OpenAiProperties
) {

    @Bean
    @Qualifier("openAiRestTemplate")
    fun openAiRestTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors.add { request, body, execution ->
            request.headers.add("Authorization", "Bearer ${openAiProperties.secretKey}")
            execution.execute(request, body)
        }
        return restTemplate
    }
}
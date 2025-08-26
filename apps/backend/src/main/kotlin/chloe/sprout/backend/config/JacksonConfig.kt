package chloe.sprout.backend.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.openapitools.jackson.nullable.JsonNullableModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        val javaTimeModule = JavaTimeModule()
            .addSerializer(
                LocalDateTime::class.java,
                object : JsonSerializer<LocalDateTime>() {
                    override fun serialize(
                        value: LocalDateTime,
                        gen: JsonGenerator,
                        serializers: SerializerProvider
                    ) {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        gen.writeString(value.format(formatter))
                    }
                }
            )

        return jacksonObjectMapper().apply {
            registerModule(kotlinModule())
            registerModule(javaTimeModule)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)

            // JsonNullableModule 관련
            setSerializationInclusion(JsonInclude.Include.ALWAYS)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            registerModule(JsonNullableModule())
        }
    }
}
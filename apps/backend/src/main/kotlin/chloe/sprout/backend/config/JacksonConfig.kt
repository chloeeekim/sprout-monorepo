package chloe.sprout.backend.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
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

        return ObjectMapper()
            .registerModule(kotlinModule())
            .registerModule(javaTimeModule)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    }
}
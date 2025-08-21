package chloe.sprout.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
class JpaAuditingConfig {
    @Bean
    fun auditingDateTimeProvider(): DateTimeProvider =
        DateTimeProvider {
            Optional.of(OffsetDateTime.now(ZoneId.of("Asia/Seoul")))
        }
}
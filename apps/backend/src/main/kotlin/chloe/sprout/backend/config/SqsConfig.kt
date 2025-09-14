package chloe.sprout.backend.config

import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
@Profile("prod")
class SqsConfig {

    @Bean
    fun sqsClient(): SqsClient {
        return SqsClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }

    @Bean
    fun sqsTemplate(sqsClient: SqsClient): SqsTemplate {
        return sqsTemplate(sqsClient)
    }
}
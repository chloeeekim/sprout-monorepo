package chloe.sprout.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.http.apache5.Apache5HttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import java.time.Duration

@Configuration
@Profile("prod")
class AwsConfig {

    @Bean
    fun sqsClient(): SqsClient {
        val httpClient = Apache5HttpClient.builder()
            .maxConnections(100)
            .connectionAcquisitionTimeout(Duration.ofSeconds(30))
            .build()

        return SqsClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .httpClient(httpClient)
            .build()
    }
}
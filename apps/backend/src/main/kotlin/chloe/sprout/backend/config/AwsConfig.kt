package chloe.sprout.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import java.time.Duration

@Configuration
@Profile("prod")
class AwsConfig {

    @Bean
    fun sqsHttpClient(): SdkAsyncHttpClient {
        return NettyNioAsyncHttpClient.builder()
            .connectionAcquisitionTimeout(Duration.ofSeconds(30))
            .connectionTimeout(Duration.ofSeconds(10))
            .build()
    }

    @Bean
    @Primary
    fun sqsAsyncClient(sqsHttpClient: SdkAsyncHttpClient): SqsAsyncClient {
        return SqsAsyncClient.builder()
            .httpClient(sqsHttpClient)
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }
}
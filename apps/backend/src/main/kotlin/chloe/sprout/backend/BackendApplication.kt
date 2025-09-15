package chloe.sprout.backend

import chloe.sprout.backend.property.OpenAiProperties
import io.awspring.cloud.autoconfigure.sqs.SqsAutoConfiguration
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider

@SpringBootApplication(exclude = [SqsAutoConfiguration::class])
@EnableConfigurationProperties(OpenAiProperties::class)
class BackendApplication

fun main(args: Array<String>) {
	runApplication<BackendApplication>(*args)
}

@Component
class Runner : CommandLineRunner {
    override fun run(vararg args: String?) {
        println("=== Check Credentials Start ===")

        val creds = DefaultCredentialsProvider.create().resolveCredentials()
        println("=== AccessKeyId: ${creds.accessKeyId().toString()}")

        println("=== Check Credentials End ===")
    }
}
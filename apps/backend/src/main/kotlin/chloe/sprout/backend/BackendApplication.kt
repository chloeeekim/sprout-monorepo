package chloe.sprout.backend

import chloe.sprout.backend.property.OpenAiProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(OpenAiProperties::class)
class BackendApplication

fun main(args: Array<String>) {
	runApplication<BackendApplication>(*args)
}
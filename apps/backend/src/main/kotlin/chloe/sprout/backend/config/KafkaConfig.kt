package chloe.sprout.backend.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff

@Configuration
@Profile("kafka")
class KafkaConfig {

    companion object {
        private const val NOTE_UPDATED_TOPIC = "note.updated"
    }

    @Bean
    fun noteUpdatedTopic(): NewTopic {
        return TopicBuilder.name(NOTE_UPDATED_TOPIC)
            .partitions(3) // 파티션 개수
            .replicas(1) // 복제본 개수 (로컬에서는 1, 운영 환경에서는 3 이상으로 설정)
            .build()
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Any>,
        kafkaTemplate: KafkaTemplate<String, Any>,
        kafkaProperties: KafkaProperties
    ): ConcurrentKafkaListenerContainerFactory<String, Any> {
        // Dead Letter Queue(DLQ) 설정
        // 3번의 시도 후에도 실패하면 DLQ로 메시지 발행
        val errorHandler = DefaultErrorHandler(
            DeadLetterPublishingRecoverer(kafkaTemplate),
            FixedBackOff(1000L, 2) // 1초 간격으로 최대 2번 재시도
        )

        // 컨슈머 동시성 및 기타 설정
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory
        factory.setCommonErrorHandler(errorHandler)
        factory.setConcurrency(3) // 파티션 개수와 동일하게 컨슈머 스레드 개수 설정

        return factory
    }
}
package chloe.sprout.backend.config

import chloe.sprout.backend.service.NoteService
import chloe.sprout.backend.service.UserService
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@TestConfiguration
@Profile("test-unit")
class MockServiceConfig {
    @Bean
    fun userService() = mockk<UserService>()

    @Bean
    fun noteService() = mockk<NoteService>()
}
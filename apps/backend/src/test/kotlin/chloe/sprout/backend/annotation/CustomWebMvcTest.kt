package chloe.sprout.backend.annotation

import chloe.sprout.backend.auth.JwtAuthenticationFilter
import chloe.sprout.backend.config.SecurityConfig
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@WebMvcTest(
    excludeAutoConfiguration = [SecurityAutoConfiguration::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [SecurityConfig::class, JwtAuthenticationFilter::class]
        )
    ]
)
annotation class CustomWebMvcTest(
    val controllers: Array<KClass<*>> = []
)

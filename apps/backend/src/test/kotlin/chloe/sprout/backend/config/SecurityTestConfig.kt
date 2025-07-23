package chloe.sprout.backend.config

import chloe.sprout.backend.property.SecurityAllowlistProperties
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter

@Profile("test-unit")
@Configuration
@EnableWebSecurity
class SecurityTestConfig {
    @Bean
    fun filterChain(http: HttpSecurity, securityAllowlistProperties: SecurityAllowlistProperties): SecurityFilterChain {
        http.csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(*securityAllowlistProperties.allowlist.toTypedArray()).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterAt(fakeAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun fakeAuthenticationFilter(): Filter {
        return object : OncePerRequestFilter() {
            override fun doFilterInternal(
                request: HttpServletRequest,
                response: HttpServletResponse,
                filterChain: FilterChain
            ) {
                filterChain.doFilter(request, response)
            }
        }
    }
}
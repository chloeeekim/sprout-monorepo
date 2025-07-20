package chloe.sprout.backend.config

import chloe.sprout.backend.auth.CustomAccessDeniedHandler
import chloe.sprout.backend.auth.CustomAuthenticationEntryPoint
import chloe.sprout.backend.auth.JwtAuthenticationFilter
import chloe.sprout.backend.property.SecurityAllowlistProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthenticationFilter,
    private val securityAllowlistProperties: SecurityAllowlistProperties,
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val accessDeniedHandler: CustomAccessDeniedHandler
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors {  } // 기본 설정
            .sessionManagement {
                // 세션 관리 상태 없음 (Spring Security가 세션 생성 또는 사용하지 않음
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // FormLogin, BasicHttp 비활성화
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            // JWT AuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            // Error Response Format 통일을 위해 Exception Handling 추가
            .exceptionHandling {
                it
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            }
            .authorizeHttpRequests {
                it
                    .requestMatchers(*securityAllowlistProperties.allowlist.toTypedArray()).permitAll()
                    .anyRequest().permitAll()
            }

        return http.build()
    }
}
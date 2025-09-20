package chloe.sprout.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
            .allowedOrigins("http://localhost:5173", "https://sproutnote.vercel.app") // frontend URL 허용
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
            .allowedHeaders("*") // 모든 헤더 허용
            .allowCredentials(true) // 자격 증명 허용
            .maxAge(3600) // pre-flight 요청 캐싱 시간 (초)
            .exposedHeaders("Authorization") // Authorization Header를 클라이언트에서 읽을 수 있도록 허용
    }
}
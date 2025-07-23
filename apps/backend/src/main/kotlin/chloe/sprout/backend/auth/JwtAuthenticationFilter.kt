package chloe.sprout.backend.auth

import chloe.sprout.backend.exception.auth.InvalidAccessTokenException
import chloe.sprout.backend.exception.auth.LoginRequiredException
import chloe.sprout.backend.exception.auth.MissingAccessTokenException
import chloe.sprout.backend.property.SecurityAllowlistProperties
import chloe.sprout.backend.service.RedisService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Profile("!test-unit")
@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val customUserDetailsService: CustomUserDetailsService,
    private val redisService: RedisService,
    private val securityAllowlistProperties: SecurityAllowlistProperties,
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint
) : OncePerRequestFilter() {

    // 인증이 필요 없는 경로는 제외
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return securityAllowlistProperties.allowlist.any { path.startsWith(it) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            getJwtFromRequest(request)?.let {
                // denylist에 등록된 경우
                val denylist = redisService.getDenylist(it)
                if (denylist != null && denylist == "logout") {
                    throw LoginRequiredException()
                }

                // access token이 유효하지 않은 경우
                if (!jwtTokenProvider.validateToken(it)) {
                    throw InvalidAccessTokenException()
                }

                // 인증 정보 등록
                val email = jwtTokenProvider.getEmailFromToken(it)
                val userDetails = customUserDetailsService.loadUserByUsername(email)
                val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            } ?: throw MissingAccessTokenException()

            filterChain.doFilter(request, response)
        } catch (ex: AuthenticationException) {
            SecurityContextHolder.clearContext()
            authenticationEntryPoint.commence(request, response, ex)
        }
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7, bearerToken.length)
        } else null
    }
}
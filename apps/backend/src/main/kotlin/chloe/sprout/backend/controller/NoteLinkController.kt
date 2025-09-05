package chloe.sprout.backend.controller

import chloe.sprout.backend.auth.CustomUserDetails
import chloe.sprout.backend.dto.NoteLinkResponse
import chloe.sprout.backend.service.NoteLinkService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/links")
class NoteLinkController(
    private val noteLinkService: NoteLinkService
) {
    @GetMapping
    fun getNoteLinks(@AuthenticationPrincipal user: CustomUserDetails): ResponseEntity<List<NoteLinkResponse>> {
        val response = noteLinkService.findAllLinks(user.getUserId())
        return ResponseEntity.ok(response)
    }
}
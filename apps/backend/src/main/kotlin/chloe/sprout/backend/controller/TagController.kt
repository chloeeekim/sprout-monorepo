package chloe.sprout.backend.controller

import chloe.sprout.backend.auth.CustomUserDetails
import chloe.sprout.backend.dto.TagListResponse
import chloe.sprout.backend.service.TagService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tags")
class TagController(
    private val tagService: TagService
) {
    @GetMapping
    fun getTagsByOwnerId(@AuthenticationPrincipal user: CustomUserDetails): ResponseEntity<List<TagListResponse>> {
        val response = tagService.getAllTagsByOwnerId(user.getUserId())
        return ResponseEntity.ok(response)
    }
}
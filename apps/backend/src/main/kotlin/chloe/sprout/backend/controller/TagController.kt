package chloe.sprout.backend.controller

import chloe.sprout.backend.auth.CustomUserDetails
import chloe.sprout.backend.dto.TagCreateRequest
import chloe.sprout.backend.dto.TagCreateResponse
import chloe.sprout.backend.dto.TagListResponse
import chloe.sprout.backend.service.TagService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/tags")
class TagController(
    private val tagService: TagService
) {
    @PostMapping
    fun createTag(@AuthenticationPrincipal user: CustomUserDetails, @RequestBody request: TagCreateRequest): ResponseEntity<TagCreateResponse> {
        val response = tagService.createTag(user.getUserId(), request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getTagsByOwnerId(@AuthenticationPrincipal user: CustomUserDetails): ResponseEntity<List<TagListResponse>> {
        val response = tagService.getAllTagsByOwnerId(user.getUserId())
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteTag(@AuthenticationPrincipal user: CustomUserDetails, @PathVariable id: UUID): ResponseEntity<Void> {
        tagService.deleteTag(user.getUserId(), id)
        return ResponseEntity.noContent().build()
    }
}
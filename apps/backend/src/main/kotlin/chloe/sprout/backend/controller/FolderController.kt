package chloe.sprout.backend.controller

import chloe.sprout.backend.auth.CustomUserDetails
import chloe.sprout.backend.dto.*
import chloe.sprout.backend.service.FolderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/folders")
class FolderController(
    private val folderService: FolderService
) {
    @PostMapping
    fun createFolder(@AuthenticationPrincipal user: CustomUserDetails, @RequestBody @Valid request: FolderCreateRequest): ResponseEntity<FolderCreateResponse> {
        val response = folderService.createFolder(user.getUserId(), request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getFolders(@AuthenticationPrincipal user: CustomUserDetails): ResponseEntity<List<FolderListResponse>> {
        val response = folderService.getFolders(user.getUserId())
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{folderId}")
    fun updateFolder(@AuthenticationPrincipal user: CustomUserDetails, @PathVariable folderId: UUID, @RequestBody @Valid request: FolderUpdateRequest): ResponseEntity<FolderUpdateResponse> {
        val response = folderService.updateFolder(user.getUserId(), folderId, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{folderId}")
    fun deleteFolder(@AuthenticationPrincipal user: CustomUserDetails, @PathVariable folderId: UUID): ResponseEntity<Void> {
        folderService.deleteFolder(user.getUserId(), folderId)
        return ResponseEntity.noContent().build()
    }
}
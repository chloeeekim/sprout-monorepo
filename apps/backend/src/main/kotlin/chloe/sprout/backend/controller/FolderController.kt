package chloe.sprout.backend.controller

import chloe.sprout.backend.auth.CustomUserDetails
import chloe.sprout.backend.dto.FolderCreateRequest
import chloe.sprout.backend.dto.FolderCreateResponse
import chloe.sprout.backend.dto.FolderListResponse
import chloe.sprout.backend.dto.FolderUpdateRequest
import chloe.sprout.backend.dto.FolderUpdateResponse
import chloe.sprout.backend.service.FolderService
import jakarta.validation.Valid
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
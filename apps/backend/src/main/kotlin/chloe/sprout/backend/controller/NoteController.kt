package chloe.sprout.backend.controller

import chloe.sprout.backend.auth.CustomUserDetails
import chloe.sprout.backend.dto.*
import chloe.sprout.backend.service.NoteService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val noteService: NoteService
) {
    @PostMapping
    fun createNote(@AuthenticationPrincipal user: CustomUserDetails, @RequestBody @Valid request: NoteCreateRequest): ResponseEntity<NoteCreateResponse> {
        val response = noteService.createNote(user.getUserId(), request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getNoteById(@AuthenticationPrincipal user: CustomUserDetails, @PathVariable id: UUID): ResponseEntity<NoteDetailResponse> {
        val response = noteService.getNoteById(id, user.getUserId())
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAllNotes(@AuthenticationPrincipal user: CustomUserDetails,
                    @RequestParam(required = false) tag: String?, @RequestParam(required = false) keyword: String?): ResponseEntity<List<NoteListResponse>> {
        val response = noteService.getAllNotesByUserId(user.getUserId(), tag, keyword)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}")
    fun updateNote(@AuthenticationPrincipal user: CustomUserDetails, @PathVariable id: UUID, @RequestBody @Valid request: NoteUpdateRequest): ResponseEntity<NoteUpdateResponse> {
        val response = noteService.updateNote(user.getUserId(), id, request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/favorite")
    fun toggleIsFavorite(@AuthenticationPrincipal user: CustomUserDetails, @PathVariable id: UUID): ResponseEntity<NoteUpdateResponse> {
        val response = noteService.toggleIsFavorite(user.getUserId(), id)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteNote(@AuthenticationPrincipal user: CustomUserDetails, @PathVariable id: UUID): ResponseEntity<Void> {
        noteService.deleteNote(user.getUserId(), id)
        return ResponseEntity.noContent().build()
    }
}
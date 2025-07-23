package chloe.sprout.backend.controller

import chloe.sprout.backend.auth.CustomUserDetails
import chloe.sprout.backend.config.MockServiceConfig
import chloe.sprout.backend.config.SecurityTestConfig
import chloe.sprout.backend.domain.Note
import chloe.sprout.backend.domain.User
import chloe.sprout.backend.dto.*
import chloe.sprout.backend.exception.global.GlobalErrorCode
import chloe.sprout.backend.exception.global.InvalidFieldValueException
import chloe.sprout.backend.exception.note.NoteErrorCode
import chloe.sprout.backend.exception.note.NoteNotFoundException
import chloe.sprout.backend.exception.note.NoteOwnerMismatchException
import chloe.sprout.backend.exception.note.NoteTitleRequiredException
import chloe.sprout.backend.exception.user.UserErrorCode
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.service.NoteService
import chloe.sprout.backend.util.TestUtils
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import java.util.*
import org.hamcrest.Matchers.hasSize

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityTestConfig::class, MockServiceConfig::class)
@ActiveProfiles("test-unit")
class NoteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var noteService: NoteService

    private lateinit var testUser: User
    private lateinit var invalidTestUser: User
    private lateinit var testNote: Note

    @BeforeEach
    fun setUp() {
        clearAllMocks()

        testUser = User(
            email = "test@test.com",
            name = "name",
            password = "password"
        )
        TestUtils.setSuperClassPrivateField(testUser, "id", UUID.fromString("99999999-9999-9999-9999-999999999999"))
        TestUtils.setSuperClassPrivateField(testUser, "createdAt", LocalDateTime.now())
        TestUtils.setSuperClassPrivateField(testUser, "updatedAt", LocalDateTime.now())

        invalidTestUser = User(
            email = "invalid@test.com",
            name = "name",
            password = "password"
        )
        TestUtils.setSuperClassPrivateField(invalidTestUser, "id", UUID.fromString("00000000-0000-0000-0000-000000000000"))
        TestUtils.setSuperClassPrivateField(invalidTestUser, "createdAt", LocalDateTime.now())
        TestUtils.setSuperClassPrivateField(invalidTestUser, "updatedAt", LocalDateTime.now())

        testNote = Note(
            title = "Test note",
            content = "This is a test note content.",
            owner = testUser
        )
        TestUtils.setSuperClassPrivateField(testNote, "id", UUID.fromString("99999999-9999-9999-9999-999999999999"))
        TestUtils.setSuperClassPrivateField(testNote, "createdAt", LocalDateTime.now())
        TestUtils.setSuperClassPrivateField(testNote, "updatedAt", LocalDateTime.now())
    }

    @Test
    @DisplayName("POST /api/notes (노트 생성) - 성공")
    fun createNoteApi_success() {
        // given
        val tagNames = listOf("tag1", "tag2")
        val request = NoteCreateRequest(testNote.title, testNote.content, tags = tagNames)
        val response =NoteCreateResponse(
            id = testNote.id,
            title = testNote.title,
            content = testNote.content,
            isFavorite = false,
            tags = tagNames,
            createdAt = requireNotNull(testNote.createdAt)
        )

        every { noteService.createNote(any(), any()) } returns response

        // when & then
        mockMvc.post("/api/notes") {
            with(user(CustomUserDetails(testUser)))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.data.id") { value(testNote.id.toString()) }
            jsonPath("$.data.title") { value(testNote.title) }
            jsonPath("$.data.content") { value(testNote.content) }
            jsonPath("$.data.isFavorite") { value(false) }
            jsonPath("$.data.tags", hasSize<Any>(tagNames.size))
        }

        verify(exactly = 1) { noteService.createNote(testUser.id, request) }
    }

    @Test
    @DisplayName("POST /api/notes (노트 생성) - 실패 (사용자 없음)")
    fun createNoteApi_fail_userNotFound() {
        // given
        val request = NoteCreateRequest(testNote.title, testNote.content)
        val errorDetail = UserErrorCode.USER_NOT_FOUND.getErrorDetail()

        every { noteService.createNote(testUser.id, request) } throws UserNotFoundException()

        // when & then
        mockMvc.post("/api/notes") {
            with(user(CustomUserDetails(testUser)))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { noteService.createNote(testUser.id, request) }
    }

    @Test
    @DisplayName("POST /api/notes (노트 생성) - 실패 (빈 타이틀)")
    fun createNoteApi_fail_noteTitleRequired() {
        // given
        val request = NoteCreateRequest("", testNote.content)
        val errorDetail = GlobalErrorCode.INVALID_FIELD_VALUE.getErrorDetail()

        every { noteService.createNote(testUser.id, request) } throws NoteTitleRequiredException()

        // when & then
        mockMvc.post("/api/notes") {
            with(user(CustomUserDetails(testUser)))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 0) { noteService.createNote(testUser.id, request) }
    }

    @Test
    @DisplayName("GET /api/notes/{id} (노트 ID로 조회) - 성공")
    fun getNoteByIdApi_success() {
        // given
        val noteId = testNote.id
        val response = NoteDetailResponse(
            id = testNote.id,
            title = testNote.title,
            content = testNote.content,
            isFavorite = false,
            tags = emptyList(),
            updatedAt = requireNotNull(testNote.updatedAt)
        )

        every { noteService.getNoteById(noteId, testUser.id) } returns response

        // when & then
        mockMvc.get("/api/notes/{id}", noteId) {
            with(user(CustomUserDetails(testUser)))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(response)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.id") { value(testNote.id.toString()) }
            jsonPath("$.data.title") { value(testNote.title) }
            jsonPath("$.data.content") { value(testNote.content) }
        }

        verify(exactly = 1) { noteService.getNoteById(noteId, testNote.id) }
    }

    @Test
    @DisplayName("GET /api/notes/{id} (노트 ID로 조회) - 실패 (노트 없음)")
    fun getNoteByIdApi_fail_noteNotFound() {
        // given
        val invalidNoteId = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val errorDetail = NoteErrorCode.NOTE_NOT_FOUND.getErrorDetail()

        every { noteService.getNoteById(invalidNoteId, testUser.id) } throws NoteNotFoundException()

        // when & then
        mockMvc.get("/api/notes/{id}", invalidNoteId) {
            with(user(CustomUserDetails(testUser)))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(errorDetail)
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { noteService.getNoteById(invalidNoteId, testUser.id) }
    }

    @Test
    @DisplayName("GET /api/notes/{id} (노트 ID로 조회) - 실패 (소유자 불일치)")
    fun getNoteByIdApi_fail_noteOwnerMismatch() {
        // given
        val invalidUserId = invalidTestUser.id
        val errorDetail = NoteErrorCode.NOTE_OWNER_MISMATCH.getErrorDetail()

        every { noteService.getNoteById(testNote.id, invalidUserId) } throws NoteOwnerMismatchException()

        // when & then
        mockMvc.get("/api/notes/{id}", testNote.id) {
            with(user(CustomUserDetails(invalidTestUser)))
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { noteService.getNoteById(testNote.id, invalidUserId) }
    }

    @Test
    @DisplayName("GET /api/notes (사용자 ID로 모든 노트 조회) - 성공")
    fun getAllNotesApi_success() {
        // given
        val response = NoteListResponse(
            id = testNote.id,
            title = testNote.title,
            content = testNote.content,
            isFavorite = false,
            tags = emptyList(),
            updatedAt = requireNotNull(testNote.updatedAt)
        )

        every { noteService.getAllNotesByUserId(testUser.id) } returns listOf(response)

        // when & then
        mockMvc.get("/api/notes") {
            with(user(CustomUserDetails(testUser)))
        }.andExpect {
            status { isOk() }
            jsonPath("$.data[0].id") { value(testNote.id.toString()) }
            jsonPath("$.data[0].title") { value(testNote.title) }
            jsonPath("$.data[0].content") { value(testNote.content) }
        }

        verify(exactly = 1) { noteService.getAllNotesByUserId(testUser.id) }
    }

    @Test
    @DisplayName("POST /api/notes/{id} (노트 업데이트) - 성공")
    fun updateNoteApi_success() {
        // given
        val updatedTitle = "Updated note"
        val updatedContent = "This is a updated note content."
        val tagNames = listOf("tag1", "tag2")
        val request = NoteUpdateRequest(updatedTitle, updatedContent, tags = tagNames)
        val response = NoteUpdateResponse(
            id = testNote.id,
            title = updatedTitle,
            content = updatedContent,
            isFavorite = false,
            tags = tagNames,
            updatedAt = LocalDateTime.now()
        )

        every { noteService.updateNote(testUser.id, testNote.id, request) } returns response

        // when & then
        mockMvc.post("/api/notes/{id}", testNote.id) {
            with(user(CustomUserDetails(testUser)))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(response)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.id") { value(testNote.id.toString()) }
            jsonPath("$.data.title") { value(updatedTitle) }
            jsonPath("$.data.content") { value(updatedContent) }
            jsonPath("$.data.isFavorite") { value(false) }
            jsonPath("$.data.tags", hasSize<Any>(tagNames.size))
        }

        verify(exactly = 1) { noteService.updateNote(testUser.id, testNote.id, request) }
    }

    @Test
    @DisplayName("POST /api/notes/{id} (노트 업데이트) - 실패 (노트 없음)")
    fun updateNoteApi_fail_noteNotFound() {
        // given
        val updatedTitle = "Updated note"
        val updatedContent = "This is a updated note content."
        val request = NoteUpdateRequest(updatedTitle, updatedContent)
        val errorDetail = NoteErrorCode.NOTE_NOT_FOUND.getErrorDetail()

        every { noteService.updateNote(testUser.id, testNote.id, request) } throws NoteNotFoundException()

        // when & then
        mockMvc.post("/api/notes/{id}", testNote.id) {
            with(user(CustomUserDetails(testUser)))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { noteService.updateNote(testUser.id, testNote.id, request) }
    }

    @Test
    @DisplayName("POST /api/notes/{id} (노트 업데이트) - 실패 (소유자 불일치)")
    fun updateNoteApi_fail_noteOwnerMismatch() {
        // given
        val updatedTitle = "Updated note"
        val updatedContent = "This is a updated note content."
        val request = NoteUpdateRequest(updatedTitle, updatedContent)
        val errorDetail = NoteErrorCode.NOTE_OWNER_MISMATCH.getErrorDetail()

        every { noteService.updateNote(any(), any(), any()) } throws NoteOwnerMismatchException()

        // when & then
        mockMvc.post("/api/notes/{id}", testNote.id) {
            with(user(CustomUserDetails(invalidTestUser)))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { noteService.updateNote(invalidTestUser.id, testNote.id, request) }
    }

    @Test
    @DisplayName("POST /api/notes/{id} (노트 업데이트) - 실패 (빈 타이틀)")
    fun updateNoteApi_fail_noteTitleRequired() {
        // given
        val updatedContent = "This is a updated note content."
        val request = NoteUpdateRequest("", updatedContent)
        val errorDetail = GlobalErrorCode.INVALID_FIELD_VALUE.getErrorDetail()

        every { noteService.updateNote(any(), any(), any()) } throws InvalidFieldValueException()

        // when & then
        mockMvc.post("/api/notes/{id}", testNote.id) {
            with(user(CustomUserDetails(testUser)))
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 0) { noteService.updateNote(testUser.id, testNote.id, request) }
    }

    @Test
    @DisplayName("POST /api/notes/{id}/favorite (즐겨찾기 상태 토글) - 성공")
    fun toggleIsFavorite_success() {
        // given
        val updatedNote = testNote.apply { isFavorite = true }
        val response = NoteUpdateResponse.from(updatedNote)

        every { noteService.toggleIsFavorite(testUser.id, testNote.id) } returns response

        // when & then
        mockMvc.post("/api/notes/{id}/favorite", testNote.id) {
            with(user(CustomUserDetails(testUser)))
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.id") { value(testNote.id.toString()) }
            jsonPath("$.data.isFavorite") { value(true) }
        }

        verify(exactly = 1) { noteService.toggleIsFavorite(testUser.id, testNote.id) }
    }

    @Test
    @DisplayName("POST /api/notes/{id}/favorite (즐겨찾기 상태 토글) - 실패 (노트 없음)")
    fun toggleIsFavorite_fail_noteNotFound() {
        // given
        val errorDetail = NoteErrorCode.NOTE_NOT_FOUND.getErrorDetail()

        every { noteService.toggleIsFavorite(testUser.id, testNote.id) } throws NoteNotFoundException()

        // when & then
        mockMvc.post("/api/notes/{id}/favorite", testNote.id) {
            with(user(CustomUserDetails(testUser)))
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { noteService.toggleIsFavorite(testUser.id, testNote.id) }
    }

    @Test
    @DisplayName("POST /api/notes/{id}/favorite (즐겨찾기 상태 토글) - 실패 (소유자 불일치")
    fun toggleIsFavorite_fail_noteOwnerMismatch() {
        // given
        val errorDetail = NoteErrorCode.NOTE_OWNER_MISMATCH.getErrorDetail()

        every { noteService.toggleIsFavorite(invalidTestUser.id, testNote.id) } throws NoteOwnerMismatchException()

        // when & then
        mockMvc.post("/api/notes/{id}/favorite", testNote.id) {
            with(user(CustomUserDetails(invalidTestUser)))
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { noteService.toggleIsFavorite(invalidTestUser.id, testNote.id) }
    }

    @Test
    @DisplayName("DELETE /api/notes/{id} (노트 삭제) - 성공")
    fun deleteNoteApi_success() {
        // given

        every { noteService.deleteNote(testUser.id, testNote.id) } returns Unit

        // when & then
        mockMvc.delete("/api/notes/{id}", testNote.id) {
            with(user(CustomUserDetails(testUser)))
        }.andExpect {
            status { isNoContent() }
        }

        verify(exactly = 1) { noteService.deleteNote(testUser.id, testNote.id) }
    }

    @Test
    @DisplayName("DELETE /api/notes/{id} (노트 삭제) - 실패 (노트 없음)")
    fun deleteNoteApi_fail_noteNotFound() {
        // given
        val errorDetail = NoteErrorCode.NOTE_NOT_FOUND.getErrorDetail()

        every { noteService.deleteNote(testUser.id, testNote.id) } throws NoteNotFoundException()

        // when & then
        mockMvc.delete("/api/notes/{id}", testNote.id) {
            with(user(CustomUserDetails(testUser)))
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { noteService.deleteNote(testUser.id, testNote.id) }
    }

    @Test
    @DisplayName("DELETE /api/notes/{id} (노트 삭제) - 실패 (소유자 불일치)")
    fun deleteNoteApi_fail_noteOwnerMismatch() {
        // given
        val errorDetail = NoteErrorCode.NOTE_OWNER_MISMATCH.getErrorDetail()

        every { noteService.deleteNote(any(), any()) } throws NoteOwnerMismatchException()

        // when & then
        mockMvc.delete("/api/notes/{id}", testNote.id) {
            with(user(CustomUserDetails(invalidTestUser)))
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { noteService.deleteNote(invalidTestUser.id, testNote.id) }
    }
}
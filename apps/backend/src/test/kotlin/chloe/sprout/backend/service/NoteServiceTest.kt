package chloe.sprout.backend.service

import chloe.sprout.backend.domain.Note
import chloe.sprout.backend.domain.NoteTag
import chloe.sprout.backend.domain.Tag
import chloe.sprout.backend.domain.User
import chloe.sprout.backend.dto.NoteCreateRequest
import chloe.sprout.backend.dto.NoteUpdateRequest
import chloe.sprout.backend.exception.note.NoteNotFoundException
import chloe.sprout.backend.exception.note.NoteOwnerMismatchException
import chloe.sprout.backend.exception.note.NoteTitleRequiredException
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.repository.NoteRepository
import chloe.sprout.backend.repository.TagRepository
import chloe.sprout.backend.repository.UserRepository
import chloe.sprout.backend.util.TestUtils
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class NoteServiceTest {

    @MockK
    private lateinit var noteRepository: NoteRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var tagRepository: TagRepository

    @InjectMockKs
    private lateinit var noteService: NoteService

    private lateinit var testUser: User
    private lateinit var testNote: Note

    @BeforeEach
    fun setUp() {
        testUser = User(
            email = "test@test.com",
            name = "name",
            password = "password"
        )
        TestUtils.setSuperClassPrivateField(testUser, "id", UUID.fromString("99999999-9999-9999-9999-999999999999"))

        testNote = Note(
            title = "Test note",
            content = "This is a test note content.",
            owner = testUser,
            isFavorite = false,
            noteTags = mutableSetOf()
        )
        TestUtils.setSuperClassPrivateField(testNote, "id", UUID.fromString("99999999-9999-9999-9999-999999999999"))
        TestUtils.setSuperClassPrivateField(testNote, "createdAt", LocalDateTime.now())
        TestUtils.setSuperClassPrivateField(testNote, "updatedAt", LocalDateTime.now())
    }

    @Test
    @DisplayName("노트 생성 - 성공")
    fun createNote_success() {
        // given
        val tagNames = listOf("tag1", "tag2")
        val request = NoteCreateRequest(testNote.title, testNote.content, tags = tagNames)
        val tag1 = Tag(tagNames[0], testUser)
        val tag2 = Tag(tagNames[1], testUser)
        testNote.noteTags.add(NoteTag(testNote, tag1))
        testNote.noteTags.add(NoteTag(testNote, tag2))

        every { userRepository.findByIdOrNull(testUser.id) } returns testUser
        every { tagRepository.findByNameAndOwner(any(), testUser) } returns null
        every { noteRepository.save(any()) } returns testNote

        // when
        val response = noteService.createNote(testUser.id, request)

        // then
        assertThat(response).isNotNull
        assertThat(response.id).isEqualTo(testNote.id)
        assertThat(response.title).isEqualTo(request.title)
        assertThat(response.content).isEqualTo(request.content)
        assertThat(response.isFavorite).isFalse
        assertThat(response.tags).containsExactlyInAnyOrderElementsOf(tagNames)

        verify(exactly = 1) { userRepository.findByIdOrNull(testUser.id) }
        verify(exactly = tagNames.size) { tagRepository.findByNameAndOwner(any(), any()) }
        verify(exactly = 1) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("노트 생성 - 실패 (사용자 없음)")
    fun createNote_fail_userNotFound() {
        // given
        val request = NoteCreateRequest(testNote.title, testNote.content)

        every { userRepository.findByIdOrNull(testUser.id) } returns null

        // when & then
        assertThrows(UserNotFoundException::class.java) {
            noteService.createNote(testUser.id, request)
        }

        verify(exactly = 1) { userRepository.findByIdOrNull(testUser.id) }
        verify(exactly = 0) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("노트 생성 - 실패 (빈 타이틀)")
    fun createNote_fail_noteTitleRequired() {
        // given
        val request = NoteCreateRequest("", testNote.content)

        every { userRepository.findByIdOrNull(testUser.id) } returns testUser

        // when & then
        assertThrows(NoteTitleRequiredException::class.java) {
            noteService.createNote(testUser.id, request)
        }

        verify(exactly = 1) { userRepository.findByIdOrNull(testUser.id) }
        verify(exactly = 0) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("노트 ID로 조회 - 성공")
    fun getNoteById_success() {
        // given
        every { noteRepository.findByIdOrNull(testNote.id) } returns testNote

        // when
        val response = noteService.getNoteById(testNote.id, testUser.id)

        // then
        assertThat(response).isNotNull
        assertThat(response.id).isEqualTo(testNote.id)
        assertThat(response.title).isEqualTo(testNote.title)
        assertThat(response.content).isEqualTo(testNote.content)
        assertThat(response.isFavorite).isFalse
        assertThat(response.tags).isEmpty()

        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
    }

    @Test
    @DisplayName("노트 ID로 조회 - 실패 (노트 없음)")
    fun getNoteById_fail_noteNotFound() {
        // given
        every { noteRepository.findByIdOrNull(testNote.id) } returns null

        // when & then
        assertThrows(NoteNotFoundException::class.java) {
            noteService.getNoteById(testNote.id, testUser.id)
        }

        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
    }

    @Test
    @DisplayName("노트 ID로 조회 - 실패 (소유자 불일치)")
    fun getNoteById_fail_noteOwnerMismatch() {
        // given
        val invalidUserId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        every { noteRepository.findByIdOrNull(testNote.id) } returns testNote

        // when & then
        assertThrows(NoteOwnerMismatchException::class.java) {
            noteService.getNoteById(testNote.id, invalidUserId)
        }

        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
    }

    @Test
    @DisplayName("사용자 ID로 모든 노트 조회 - 성공")
    fun getAllNotesByUserId_success() {
        // given
        every { noteRepository.findAllByOwnerId(testUser.id) } returns listOf(testNote)

        // when
        val response = noteService.getAllNotesByUserId(testUser.id)

        // then
        assertThat(response).isNotNull
        assertThat(response.size).isEqualTo(1)
        assertThat(response.first().id).isEqualTo(testNote.id)
        assertThat(response.first().title).isEqualTo(testNote.title)
        assertThat(response.first().content).isEqualTo(testNote.content)
        assertThat(response.first().isFavorite).isFalse
        assertThat(response.first().tags).isEmpty()

        verify(exactly = 1) { noteRepository.findAllByOwnerId(testUser.id) }
    }

    @Test
    @DisplayName("노트 업데이트 - 성공")
    fun updateNote_success() {
        // given
        val updatedTitle = "Updated note"
        val updatedContent = "This is a updated note content."
        val tagNames = listOf("tag1", "tag2")
        val request = NoteUpdateRequest(updatedTitle, updatedContent, tags = tagNames)
        val updatedNote = Note(
            title = updatedTitle,
            content = updatedContent,
            owner = testUser
        )
        TestUtils.setSuperClassPrivateField(updatedNote, "id", testNote.id)
        TestUtils.setSuperClassPrivateField(updatedNote, "createdAt", testNote.createdAt)
        TestUtils.setSuperClassPrivateField(updatedNote, "updatedAt", LocalDateTime.now())

        every { noteRepository.findByIdOrNull(testNote.id) } returns testNote
        every { tagRepository.findByNameAndOwner(any(), testUser) } returns null
        every { noteRepository.save(any()) } answers { firstArg() }

        // when
        val response = noteService.updateNote(testUser.id, testNote.id, request)

        // then
        assertThat(response).isNotNull
        assertThat(response.id).isEqualTo(testNote.id)
        assertThat(response.title).isEqualTo(updatedTitle)
        assertThat(response.content).isEqualTo(updatedContent)
        assertThat(response.isFavorite).isFalse
        assertThat(response.tags).containsExactlyInAnyOrderElementsOf(tagNames)

        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
        verify(exactly = tagNames.size) { tagRepository.findByNameAndOwner(any(), testUser) }
        verify(exactly = 1) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("노트 업데이트 - 실패 (노트 없음)")
    fun updateNote_fail_noteNotFound() {
        // given
        val invalidNoteId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val updatedTitle = "Updated note"
        val updatedContent = "This is a updated note content."
        val request = NoteUpdateRequest(updatedTitle, updatedContent)

        every { noteRepository.findByIdOrNull(invalidNoteId) } returns null

        // when & then
        assertThrows(NoteNotFoundException::class.java) {
            noteService.updateNote(testUser.id, invalidNoteId, request)
        }

        verify(exactly = 1) { noteRepository.findByIdOrNull(invalidNoteId) }
        verify(exactly = 0) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("노트 업데이트 - 실패 (소유자 불일치)")
    fun updateNote_fail_noteOwnerMismatch() {
        // given
        val invalidUserId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val updatedTitle = "Updated note"
        val updatedContent = "This is a updated note content."
        val request = NoteUpdateRequest(updatedTitle, updatedContent)

        every { noteRepository.findByIdOrNull(testNote.id) } returns testNote

        // when & then
        assertThrows(NoteOwnerMismatchException::class.java) {
            noteService.updateNote(invalidUserId, testNote.id, request)
        }

        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
        verify(exactly = 0) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("노트 업데이트 - 실패 (빈 타이틀)")
    fun updateNote_fail_noteTitleRequired() {
        // given
        val updatedContent = "This is a updated note content."
        val request = NoteUpdateRequest("", updatedContent)

        every { noteRepository.findByIdOrNull(testNote.id) } returns testNote

        // when & then
        assertThrows(NoteTitleRequiredException::class.java) {
            noteService.updateNote(testUser.id, testNote.id, request)
        }

        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
        verify(exactly = 0) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("즐겨찾기 상태 토글 - 성공 (false -> true)")
    fun toggleIsFavorite_success_toTrue() {
        // given
        testNote.isFavorite = false
        every { noteRepository.findByIdOrNull(testNote.id) } returns testNote
        every { noteRepository.save(any()) } answers { firstArg() }

        // when
        val response = noteService.toggleIsFavorite(testUser.id, testNote.id)

        // then
        assertThat(response.id).isEqualTo(testNote.id)
        assertThat(response.isFavorite).isTrue

        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
        verify(exactly = 1) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("즐겨찾기 상태 토글 - 성공 (true -> false)")
    fun toggleIsFavorite_success_toFalse() {
        // given
        testNote.isFavorite = true
        every { noteRepository.findByIdOrNull(testNote.id) } returns testNote
        every { noteRepository.save(any()) } answers { firstArg() }

        // when
        val response = noteService.toggleIsFavorite(testUser.id, testNote.id)

        // then
        assertThat(response.id).isEqualTo(testNote.id)
        assertThat(response.isFavorite).isFalse

        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
        verify(exactly = 1) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("즐겨찾기 상태 토글 - 실패 (노트 없음)")
    fun toggleIsFavorite_fail_noteNotFound() {
        // given
        val invalidNoteId = UUID.fromString("11111111-1111-1111-1111-111111111111")

        every { noteRepository.findByIdOrNull(invalidNoteId) } returns null

        // when & then
        assertThrows(NoteNotFoundException::class.java) {
            noteService.toggleIsFavorite(testNote.id, invalidNoteId)
        }

        verify(exactly = 1) { noteRepository.findByIdOrNull(invalidNoteId) }
        verify(exactly = 0) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("즐겨찾기 상태 토글 - 실패 (소유자 불일치)")
    fun toggleIsFavorite_fail_noteOwnerMismatch() {
        // given
        val invalidUserId = UUID.fromString("11111111-1111-1111-1111-111111111111")

        every { noteRepository.findByIdOrNull(testNote.id) } returns testNote

        // when & then
        assertThrows(NoteOwnerMismatchException::class.java) {
            noteService.toggleIsFavorite(invalidUserId, testNote.id)
        }

        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
        verify(exactly = 0) { noteRepository.save(any()) }
    }

    @Test
    @DisplayName("노트 삭제 - 성공")
    fun deleteNote_success() {
        // given
        every { noteRepository.findByIdOrNull(testNote.id) } returns testNote
        every { noteRepository.delete(any()) } just Runs

        // when
        noteService.deleteNote(testUser.id, testNote.id)

        // then
        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
        verify(exactly = 1) { noteRepository.delete(any()) }
    }

    @Test
    @DisplayName("노트 삭제 - 실패 (노트 없음)")
    fun deleteNote_fail_noteNotFound() {
        // given
        val invalidNoteId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        every { noteRepository.findByIdOrNull(invalidNoteId) } returns null

        // when & then
        assertThrows(NoteNotFoundException::class.java) {
            noteService.deleteNote(testUser.id, invalidNoteId)
        }

        verify(exactly = 1) { noteRepository.findByIdOrNull(invalidNoteId) }
        verify(exactly = 0) { noteRepository.delete(any()) }
    }

    @Test
    @DisplayName("노트 삭제 - 실패 (소유자 불일치)")
    fun deleteNote_fail_noteOwnerMismatch() {
        // given
        val invalidUserId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        every { noteRepository.findByIdOrNull(testNote.id) } returns testNote

        // when & then
        assertThrows(NoteOwnerMismatchException::class.java) {
            noteService.deleteNote(invalidUserId, testNote.id)
        }

        verify(exactly = 1) { noteRepository.findByIdOrNull(testNote.id) }
        verify(exactly = 0) { noteRepository.delete(any()) }
    }
}
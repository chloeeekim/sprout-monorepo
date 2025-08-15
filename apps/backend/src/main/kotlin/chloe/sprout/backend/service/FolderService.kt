package chloe.sprout.backend.service

import chloe.sprout.backend.domain.Folder
import chloe.sprout.backend.dto.FolderCreateRequest
import chloe.sprout.backend.dto.FolderCreateResponse
import chloe.sprout.backend.dto.FolderListResponse
import chloe.sprout.backend.dto.FolderUpdateRequest
import chloe.sprout.backend.dto.FolderUpdateResponse
import chloe.sprout.backend.exception.folder.FolderNameRequiredException
import chloe.sprout.backend.exception.folder.FolderNotFoundException
import chloe.sprout.backend.exception.folder.FolderOwnerMismatchException
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.repository.FolderRepository
import chloe.sprout.backend.repository.NoteRepository
import chloe.sprout.backend.repository.UserRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FolderService(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createFolder(userId: UUID, request: FolderCreateRequest): FolderCreateResponse {
        // User 확인
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        // Folder entity 생성
        val folder = Folder(
            name = request.name,
            owner = user
        )

        // DB 저장
        val savedFolder = folderRepository.save(folder)

        // response DTO로 변환 후 반환
        return FolderCreateResponse.from(savedFolder)
    }

    @Transactional(readOnly = true)
    fun getFolders(userId: UUID): List<FolderListResponse> {
        // User 확인
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        // Folder 목록을 response DTO로 변환 후 반환
        return folderRepository.findByOwner(user).map { FolderListResponse.from(it) }
    }

    @Transactional
    fun updateFolder(userId: UUID, folderId: UUID, request: FolderUpdateRequest): FolderUpdateResponse {
        // Folder 확인
        val folder = folderRepository.findByIdOrNull(folderId)
            ?: throw FolderNotFoundException()

        // owner 일치 여부 확인
        if (folder.owner.id != userId) {
            throw FolderOwnerMismatchException()
        }

        // name blank 여부 확인
        if (request.name.isBlank()) {
            throw FolderNameRequiredException()
        }

        // Folder 내용 업데이트
        folder.name = request.name
        val savedFolder = folderRepository.save(folder)

        // response DTO로 변환 후 반환
        return FolderUpdateResponse.from(savedFolder)
    }

    @Transactional
    fun deleteFolder(userId: UUID, folderId: UUID) {
        // Folder 확인
        val folder = folderRepository.findByIdOrNull(folderId)
            ?: throw FolderNotFoundException()

        // owner 일치 여부 확인
        if (folder.owner.id != userId) {
            throw FolderOwnerMismatchException()
        }

        // Folder 삭제 전, 폴더에 속한 노트들의 연결을 제거
        folder.notes.forEach {
            it.folder = null
        }
        noteRepository.saveAll(folder.notes)

        // Folder 삭제
        folderRepository.delete(folder)
    }
}
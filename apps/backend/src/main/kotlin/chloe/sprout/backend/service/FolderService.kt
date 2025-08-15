package chloe.sprout.backend.service

import chloe.sprout.backend.domain.Folder
import chloe.sprout.backend.dto.FolderCreateRequest
import chloe.sprout.backend.dto.FolderCreateResponse
import chloe.sprout.backend.dto.FolderListResponse
import chloe.sprout.backend.dto.FolderUpdateRequest
import chloe.sprout.backend.dto.FolderUpdateResponse
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
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()
        val folder = Folder(
            name = request.name,
            owner = user
        )
        val savedFolder = folderRepository.save(folder)
        return FolderCreateResponse.from(savedFolder)
    }

    @Transactional(readOnly = true)
    fun getFolders(userId: UUID): List<FolderListResponse> {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()
        return folderRepository.findByOwner(user).map { FolderListResponse.from(it) }
    }

    @Transactional
    fun updateFolder(userId: UUID, folderId: UUID, request: FolderUpdateRequest): FolderUpdateResponse {
        val folder = folderRepository.findByIdOrNull(folderId)
            ?: throw FolderNotFoundException()
        if (folder.owner.id != userId) {
            throw FolderOwnerMismatchException()
        }
        folder.name = request.name
        val savedFolder = folderRepository.save(folder)
        return FolderUpdateResponse.from(savedFolder)
    }

    @Transactional
    fun deleteFolder(userId: UUID, folderId: UUID) {
        val folder = folderRepository.findByIdOrNull(folderId)
            ?: throw FolderNotFoundException()
        if (folder.owner.id != userId) {
            throw FolderOwnerMismatchException()
        }

        // 폴더 삭제 전, 폴더에 속한 노트들의 연결을 제거
        folder.notes.forEach {
            it.folder = null
        }
        noteRepository.saveAll(folder.notes)
        folderRepository.delete(folder)
    }
}
package chloe.sprout.backend.service

import chloe.sprout.backend.domain.Tag
import chloe.sprout.backend.dto.NoteCreateResponse
import chloe.sprout.backend.dto.TagCreateRequest
import chloe.sprout.backend.dto.TagCreateResponse
import chloe.sprout.backend.dto.TagDetailResponse
import chloe.sprout.backend.dto.TagListResponse
import chloe.sprout.backend.exception.tag.TagNameAlreadyExistsException
import chloe.sprout.backend.exception.tag.TagNameRequiredException
import chloe.sprout.backend.exception.tag.TagNotFoundException
import chloe.sprout.backend.exception.tag.TagOwnerMismatchException
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.repository.TagRepository
import chloe.sprout.backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun createTag(userId: UUID, request: TagCreateRequest): TagCreateResponse {
        // User 확인
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        // name blank 여부 확인
        if (request.name.isBlank()) {
            throw TagNameRequiredException()
        }

        // Tag name 중복 검사
        tagRepository.findByNameAndOwner(request.name, user)?.let {
            throw TagNameAlreadyExistsException()
        }

        // Tag entity 생성
        val tag = Tag(
            name = request.name,
            owner = user
        )

        // DB 저장
        val save = tagRepository.save(tag)

        // response DTO로 변환 후 반환
        return TagCreateResponse.from(save)
    }

    @Transactional
    fun getAllTagsByOwnerId(userId: UUID): List<TagListResponse> {
        // Tag 리스트를 response DTO 형태로 변환 후 응답
        return tagRepository.findAllByOwnerId(userId).map { TagListResponse.from(it) }
    }

    @Transactional
    fun deleteTag(userId: UUID, tagId: UUID) {
        // Tag 확인
        val tag = tagRepository.findByIdOrNull(tagId)
            ?: throw TagNotFoundException()

        // owner 일치 여부 확인
        if (tag.owner.id != userId) {
            throw TagOwnerMismatchException()
        }

        // Tag 삭제
        tagRepository.delete(tag)
    }
}
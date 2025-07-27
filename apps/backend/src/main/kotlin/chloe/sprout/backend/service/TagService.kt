package chloe.sprout.backend.service

import chloe.sprout.backend.dto.TagListResponse
import chloe.sprout.backend.repository.TagRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TagService(
    private val tagRepository: TagRepository
) {
    @Transactional
    fun getAllTagsByOwnerId(userId: UUID): List<TagListResponse> {
        // Tag 리스트를 response DTO 형태로 변환 후 응답
        return tagRepository.findAllByOwnerId(userId).map { TagListResponse.from(it) }
    }
}
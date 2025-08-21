package chloe.sprout.backend.dto

import chloe.sprout.backend.domain.Tag
import jakarta.validation.constraints.NotBlank
import java.util.*

data class TagCreateRequest(
    @field:NotBlank
    val name: String
)

data class TagCreateResponse(
    val id: UUID,
    val name: String
) {
    companion object {
        fun from(tag: Tag): TagCreateResponse {
            return TagCreateResponse(
                id = tag.id,
                name = tag.name
            )
        }
    }
}

data class TagDetailResponse(
    val id: UUID,
    val name: String
) {
    companion object {
        fun from(tag: Tag): TagDetailResponse {
            return TagDetailResponse(
                id = tag.id,
                name = tag.name
            )
        }
    }
}

data class TagListResponse(
    val id: UUID,
    val name: String
) {
    companion object {
        fun from(tag: Tag): TagListResponse {
            return TagListResponse(
                id = tag.id,
                name = tag.name
            )
        }
    }
}
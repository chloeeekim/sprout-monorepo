package chloe.sprout.backend.dto

import chloe.sprout.backend.domain.Tag
import java.util.UUID

data class TagListResponse(
    val id: UUID,
    val tagName: String
) {
    companion object {
        fun from(tag: Tag): TagListResponse {
            return TagListResponse(
                id = tag.id,
                tagName = tag.name
            )
        }
    }
}
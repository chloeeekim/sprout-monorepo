package chloe.sprout.backend.dto

import chloe.sprout.backend.domain.Direction
import chloe.sprout.backend.domain.NoteLink
import java.util.UUID

data class NoteLinkResponse(
    val source: UUID,
    val target: UUID,
    val label: String?,
    val direction: Direction
) {
    companion object {
        fun from(noteLink: NoteLink): NoteLinkResponse {
            return NoteLinkResponse(
                source = noteLink.sourceNote.id,
                target = noteLink.targetNote.id,
                label = noteLink.label,
                direction = noteLink.direction
            )
        }
    }
}
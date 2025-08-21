package chloe.sprout.backend.domain

import jakarta.persistence.*

@Entity
@Table(name = "note_tags",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_note_id_tag_id", columnNames = ["note_id", "tag_id"])
    ]
)
class NoteTag(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    val note: Note,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    val tag: Tag
) : AbstractPersistableEntity()
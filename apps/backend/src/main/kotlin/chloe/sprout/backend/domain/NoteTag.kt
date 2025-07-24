package chloe.sprout.backend.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "tag_id")
    val tag: Tag
) : AbstractPersistableEntity()
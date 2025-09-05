package chloe.sprout.backend.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "note_links")
class NoteLink(
    @JoinColumn(name = "owner_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var owner: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_note_id", nullable = false)
    val sourceNote: Note,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_note_id", nullable = false)
    val targetNote: Note,

    @Column(name = "label", length = 50)
    var label: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    var direction: Direction = Direction.BIDIRECTIONAL,
) : AbstractPersistableEntity()

enum class Direction {
    BIDIRECTIONAL,
    UNIDIRECTIONAL
}
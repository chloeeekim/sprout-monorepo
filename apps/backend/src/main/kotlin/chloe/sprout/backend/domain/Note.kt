package chloe.sprout.backend.domain

import jakarta.persistence.*

@Entity
@Table(name = "notes")
class Note(
    @Column(nullable = false, length = 100)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var content: String?,

    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var owner: User,

    @Column(nullable = false)
    var isFavorite: Boolean = false,

    @OneToMany(mappedBy = "note", cascade = [CascadeType.ALL], orphanRemoval = true)
    var noteTags: MutableSet<NoteTag> = mutableSetOf()
) : AbstractPersistableEntity()
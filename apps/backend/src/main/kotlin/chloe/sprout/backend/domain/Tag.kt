package chloe.sprout.backend.domain

import jakarta.persistence.*

@Entity
@Table(
    name = "tags",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_tag_name_owner", columnNames = ["name", "owner_id"])
    ]
)
class Tag(
    @Column(nullable = false, length = 50)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    var owner: User
) : AbstractPersistableEntity() {
    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true)
    var noteTags: MutableSet<NoteTag> = mutableSetOf()
}
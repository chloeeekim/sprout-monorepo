package chloe.sprout.backend.domain

import jakarta.persistence.*

@Entity
@Table(name = "folders",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_folder_name_owner", columnNames = ["name", "owner_id"])
    ]
)
class Folder(
    @Column(nullable = false, length = 100)
    var name: String,

    @JoinColumn(name = "owner_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var owner: User,

    @OneToMany(mappedBy = "folder")
    var notes: MutableList<Note> = mutableListOf()
) : AbstractPersistableEntity()
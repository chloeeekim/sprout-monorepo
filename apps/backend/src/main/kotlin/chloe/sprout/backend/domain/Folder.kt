package chloe.sprout.backend.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

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
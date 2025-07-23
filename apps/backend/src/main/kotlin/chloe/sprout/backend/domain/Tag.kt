package chloe.sprout.backend.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

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
) : AbstractPersistableEntity()
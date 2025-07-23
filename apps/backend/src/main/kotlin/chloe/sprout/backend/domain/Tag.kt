package chloe.sprout.backend.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "tags")
class Tag(
    @Column(nullable = false, unique = true, length = 50)
    var name: String
) : AbstractPersistableEntity()
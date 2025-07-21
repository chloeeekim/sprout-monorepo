package chloe.sprout.backend.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "notes")
class Note(
    @Column(nullable = false, length = 100)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var content: String?,

    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var owner: User
) : AbstractPersistableEntity()
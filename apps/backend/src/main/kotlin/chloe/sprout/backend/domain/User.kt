package chloe.sprout.backend.domain

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true, length = 100)
    val email: String,

    @Column(nullable = false, length = 255)
    val password: String,

    @Column(nullable = false, length = 50)
    val name: String
) : AbstractPersistableEntity() {
    @OneToMany(mappedBy = "owner", cascade = [(CascadeType.ALL)])
    var notes: MutableList<Note> = mutableListOf()
}
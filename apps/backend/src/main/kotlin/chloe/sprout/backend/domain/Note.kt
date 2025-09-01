package chloe.sprout.backend.domain

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

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
    var noteTags: MutableSet<NoteTag> = mutableSetOf(),

    @JoinColumn(name = "folder_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var folder: Folder? = null,

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(length = 1536)
    var embedding: FloatArray? = null,

    @Column(name = "embedding_updated_at", nullable = true)
    var embeddingUpdatedAt: OffsetDateTime? = null
) : AbstractPersistableEntity()
package chloe.sprout.backend.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "note_embeddings")
class NoteEmbedding(
    @Id
    @Column(name = "note_id")
    var id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "note_id")
    var note: Note,

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(length = 1536)
    var embedding: FloatArray? = null,

    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    var updatedAt: OffsetDateTime = OffsetDateTime.now()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = OffsetDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteEmbedding

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
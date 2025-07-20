package chloe.sprout.backend.domain

import jakarta.annotation.PostConstruct
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PostLoad
import org.hibernate.proxy.HibernateProxy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.Objects
import java.util.UUID

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AbstractPersistableEntity : Persistable<UUID> {

    @Id
    @Column(columnDefinition = "uuid")
    private val id: UUID = UUID.randomUUID()

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null

    @Transient
    private var _isNew = true

    override fun getId() = id

    override fun isNew() = _isNew

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        val otherObj = if (other is HibernateProxy) {
            other.hibernateLazyInitializer.implementation
        } else {
            other
        }

        if (otherObj !is Persistable<*>) return false

        return id == otherObj.id
    }

    override fun hashCode() = Objects.hashCode(id)

    @PostConstruct
    @PostLoad
    protected fun load() {
        _isNew = false
    }
}
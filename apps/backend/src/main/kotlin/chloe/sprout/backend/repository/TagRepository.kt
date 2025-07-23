package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TagRepository : JpaRepository<Tag, UUID> {
    fun findByName(name: String): Tag?
}
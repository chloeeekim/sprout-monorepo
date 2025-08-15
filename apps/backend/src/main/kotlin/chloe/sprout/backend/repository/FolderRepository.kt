package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.Folder
import chloe.sprout.backend.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FolderRepository : JpaRepository<Folder, UUID> {
    fun findByOwner(owner: User): List<Folder>
}
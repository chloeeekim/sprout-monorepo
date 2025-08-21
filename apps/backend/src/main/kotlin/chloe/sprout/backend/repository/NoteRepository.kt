package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.Note
import chloe.sprout.backend.repository.custom.NoteRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NoteRepository : JpaRepository<Note, UUID>, NoteRepositoryCustom {
}
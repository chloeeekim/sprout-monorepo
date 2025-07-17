package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.Refresh
import org.springframework.data.repository.CrudRepository

interface RefreshRepository : CrudRepository<Refresh, String> {
    fun findByEmail(email: String): Refresh?
}
package chloe.sprout.backend.dto

import chloe.sprout.backend.domain.Folder
import jakarta.validation.constraints.NotBlank
import java.util.*

data class FolderCreateRequest(
    @field:NotBlank
    val name: String
)

data class FolderCreateResponse(
    val id: UUID,
    val name: String,
    val count: Int
) {
    companion object {
        fun from(folder: Folder): FolderCreateResponse {
            return FolderCreateResponse(
                id = folder.id,
                name = folder.name,
                count = 0
            )
        }
    }
}

data class FolderUpdateRequest(
    @field:NotBlank
    val name: String
)

data class FolderUpdateResponse(
    val id: UUID,
    val name: String,
    val count: Int
) {
    companion object {
        fun from(folder: Folder): FolderUpdateResponse {
            return FolderUpdateResponse(
                id = folder.id,
                name = folder.name,
                count = folder.notes.size
            )
        }
    }
}

data class FolderListResponse(
    val id: UUID,
    val name: String,
    val count: Int
) {
    companion object {
        fun from(folder: Folder): FolderListResponse {
            return FolderListResponse(
                id = folder.id,
                name = folder.name,
                count = folder.notes.size
            )
        }
    }
}
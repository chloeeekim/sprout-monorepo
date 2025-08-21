package chloe.sprout.backend.repository

import chloe.sprout.backend.domain.Note
import chloe.sprout.backend.domain.QNote.note
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class NoteRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : NoteRepositoryCustom {
    override fun findNotesByOwnerId(
        userId: UUID,
        lastUpdatedAt: OffsetDateTime?,
        lastId: UUID?,
        tagId: UUID?,
        keyword: String?,
        folderId: UUID?,
        pageable: Pageable
    ): Slice<Note> {
        val size = pageable.pageSize
        val result = queryFactory
            .selectFrom(note)
            .where(
                note.owner.id.eq(userId),
                ltUpdatedAtAndId(lastUpdatedAt, lastId),
                tagCondition(tagId),
                keywordCondition(keyword),
                folderCondition(folderId)
            )
            .orderBy(note.updatedAt.desc(), note.id.desc())
            .limit((size + 1).toLong())
            .fetch()

        val hasNext = result.size > size
        if (hasNext) {
            result.removeLast()
        }

        return SliceImpl(result, pageable, hasNext);
    }

    private fun ltUpdatedAtAndId(lastUpdatedAt: OffsetDateTime?, id: UUID?): BooleanExpression? {
        if (lastUpdatedAt == null || id == null) {
            return null
        }
        return note.updatedAt.lt(lastUpdatedAt)
            .or(note.updatedAt.eq(lastUpdatedAt).and(note.id.lt(id)))
    }

    private fun tagCondition(tagId: UUID?): BooleanExpression? {
        return tagId?.let { note.noteTags.any().tag.id.eq(it) }
    }

    private fun keywordCondition(keyword: String?): BooleanExpression? {
        return if (keyword.isNullOrBlank()) {
            null
        } else {
            val titleMatch = Expressions.booleanTemplate("{0} ILIKE {1}", note.title, "%$keyword%")
            val contentMatch = Expressions.booleanTemplate("{0} ILIKE {1}", note.content, "%$keyword%")

            titleMatch.or(contentMatch)
        }
    }

    private fun folderCondition(folderId: UUID?): BooleanExpression? {
        return folderId?.let { note.folder.id.eq(it) }
    }
}
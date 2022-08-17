package com.foreverrafs.superdiary.data.local.mapper

import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.superdiary.data.local.dto.DiaryDto
import com.foreverrafs.superdiary.util.EntityMapper
import java.time.LocalDateTime

class DiaryMapper : EntityMapper<DiaryDto, Diary> {
    override fun mapToDomain(entity: DiaryDto): Diary {
        return Diary(
            message = entity.message,
            date = LocalDateTime.parse(entity.date),
            title = entity.title,
            id = entity.id
        )
    }

    override fun mapToEntity(domainModel: Diary): DiaryDto {
        return DiaryDto(
            message = domainModel.message,
            date = domainModel.date.toString(),
            title = domainModel.title,
            id = domainModel.id ?: 0L
        )
    }

    override fun mapToEntity(domainModel: List<Diary>): List<DiaryDto> {
        return domainModel.map {
            mapToEntity(it)
        }
    }

    override fun mapToDomain(entityModel: List<DiaryDto>): List<Diary> {
        return entityModel.map {
            mapToDomain(it)
        }
    }
}
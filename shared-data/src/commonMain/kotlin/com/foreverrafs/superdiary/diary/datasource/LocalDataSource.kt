package com.foreverrafs.superdiary.diary.datasource

import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

class LocalDataSource(private val database: Database) : DataSource {
    override suspend fun add(diary: Diary): Long {
        database.addDiary(diary)
        return 1
    }

    override suspend fun delete(diary: Diary): Int {
        diary.id?.let {
            database.deleteDiary(it)
            return 1
        }

        return 0
    }

    override suspend fun delete(diaries: List<Diary>): Int {
        return database.deleteDiaries(diaries.mapNotNull { it.id })
    }

    override fun fetchAll(): Flow<List<Diary>> {
        return database.getAllDiaries()
    }

    override suspend fun find(entry: String): Flow<List<Diary>> {
        return database.findDiaryByEntry(entry)
    }

    override suspend fun find(from: Instant, to: Instant): Flow<List<Diary>> {
        return database.findByDateRange(from, to)
    }

    override suspend fun findByDate(date: Instant): Flow<List<Diary>> {
        return database.findByDate(date)
    }

    override suspend fun deleteAll() {
        return database.clearDiaries()
    }
}

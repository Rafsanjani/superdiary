package com.foreverrafs.superdiary.diary.datasource

import com.foreverrafs.superdiary.Database
import com.foreverrafs.superdiary.diary.model.Diary

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

    override suspend fun fetchAll(): List<Diary> {
        return database.getAllDiaries()
    }

    override suspend fun find(query: String): List<Diary> {
        return database.findDiary(query)
    }


    override suspend fun deleteAll() {
        return database.clearDiaries()
    }
}
package com.foreverrafs.superdiary.data.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.TestDatabaseDriver
import com.foreverrafs.superdiary.data.insertRandomDiaries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllDiariesUseCaseTest {
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)

    @BeforeTest
    fun setup() {
        database.createDatabase()
        database.clearDiaries()
        Dispatchers.setMain(StandardTestDispatcher())
        insertRandomDiaries(dataSource)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Getting all diaries should return all entries`() = runTest {
        getAllDiariesUseCase().test {
            val diaries = awaitItem()

            // We inserted 30 items at the beginning
            assertThat(diaries.size).isEqualTo(30)
        }
    }
}
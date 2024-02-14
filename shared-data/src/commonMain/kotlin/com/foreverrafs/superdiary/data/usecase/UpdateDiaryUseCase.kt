package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.withContext
import okio.IOException

class UpdateDiaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(diary: Diary): Result<Boolean> = withContext(dispatchers.io) {
        try {
            Result.Success(dataSource.update(diary) != 0)
        } catch (exception: IOException) {
            Result.Failure(exception)
        }
    }
}

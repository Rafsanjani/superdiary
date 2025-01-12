package com.foreverrafs.superdiary.ui.dashboard

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.domain.usecase.GetWeeklySummaryUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.utils.DiaryPreference
import com.foreverrafs.superdiary.utils.DiarySettings
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    private lateinit var dataSource: DataSource

    private val diaryAI: DiaryAI = mock<DiaryAI>()

    private val diaryPreference: DiaryPreference = mock<DiaryPreference> {
        everySuspend { save(any()) }.returns(Unit)
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
        dataSource = mock()

        every { dataSource.fetchAll() }.returns(flowOf())
        everySuspend { diaryPreference.save(any()) }.returns(Unit)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createDashboardViewModel(): DashboardViewModel = DashboardViewModel(
        getAllDiariesUseCase = GetAllDiariesUseCase(dataSource, TestAppDispatchers),
        calculateStreakUseCase = CalculateStreakUseCase(TestAppDispatchers),
        addWeeklySummaryUseCase = AddWeeklySummaryUseCase(dataSource, TestAppDispatchers),
        getWeeklySummaryUseCase = GetWeeklySummaryUseCase(dataSource, TestAppDispatchers),
        diaryAI = diaryAI,
        calculateBestStreakUseCase = CalculateBestStreakUseCase(TestAppDispatchers),
        updateDiaryUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers),
        logger = AggregateLogger(emptyList()),
        preference = diaryPreference,
        clock = Clock.System,
    )

    @Test
    fun `Should load dashboard from loading state`() = runTest {
        val viewModel = createDashboardViewModel()
        viewModel.state.test {
            val state = awaitItem()

            assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Loading>()
        }
    }

    @Test
    fun `Should generate weekly summary if diaries isn't empty`() = runTest {
        every { dataSource.fetchAll() }.returns(
            flowOf(
                listOf(Diary("Hello World")),
            ),
        )
        everySuspend { dataSource.getWeeklySummary() }.returns(
            WeeklySummary("This is your weekly summary"),
        )

        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            skipItems(2)
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Content>()
            val content: DashboardViewModel.DashboardScreenState.Content =
                state as DashboardViewModel.DashboardScreenState.Content

            assertThat(content.weeklySummary!!).contains("This is your weekly summary")
        }
    }

    @Test
    fun `Should only generate weekly summary every week`() = runTest {
        every { dataSource.fetchAll() }.returns(
            flowOf(listOf(Diary("Hello World"))),
        )

        every { dataSource.getWeeklySummary() }.returns(
            WeeklySummary(
                summary = "Old diary summary",
                date = Clock.System.now().minus(
                    value = 5,
                    unit = DateTimeUnit.DAY,
                    timeZone = TimeZone.UTC,
                ),
            ),
        )
        every { diaryAI.generateSummary(any()) }.returns(flowOf("New Diary Summary"))

        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            // Skip the loading state
            skipItems(2)
            val state = awaitItem() as? DashboardViewModel.DashboardScreenState.Content

            cancelAndIgnoreRemainingEvents()
            assertThat(state).isNotNull()
            assertThat(state?.weeklySummary).isEqualTo("Old diary summary")
        }
    }

    @Test
    fun `Should generate weekly summary when weekly summary is older than a week`() = runTest {
        every { dataSource.fetchAll() }.returns(flowOf(listOf(Diary("Hello World"))))
        everySuspend { dataSource.insertWeeklySummary(any()) }.returns(Unit)
        every { dataSource.getWeeklySummary() }.returns(
            WeeklySummary(
                summary = "Old diary summary",
                date = Clock.System.now().minus(value = 20, unit = DateTimeUnit.DAY, TimeZone.UTC),
            ),
        )
        every { diaryAI.generateSummary(any()) }.returns(flowOf("New Diary Summary"))

        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            // Skip the loading state
            skipItems(2)
            val state = awaitItem() as? DashboardViewModel.DashboardScreenState.Content

            cancelAndIgnoreRemainingEvents()
            assertThat(state).isNotNull()
            assertThat(state?.weeklySummary).isEqualTo("New Diary Summary")
        }
    }

    @Test
    fun `Should save settings when dashboard ordering is changed`() = runTest {
        val viewModel = createDashboardViewModel()

        viewModel.updateSettings(DiarySettings.Empty)
        delay(100)
        verifySuspend { diaryPreference.save(any()) }
    }

    @Test
    fun `Should toggle favorite when favorite is toggled`() = runTest {
        val diary = Diary("Hello World")
        every { dataSource.fetchAll() }.returns(flowOf(listOf(diary)))
        everySuspend { dataSource.update(any()) }.returns(1)

        val viewModel = createDashboardViewModel()

        viewModel.toggleFavorite(diary)

        verifySuspend { dataSource.update(any()) }
    }

    @Test
    fun `Should fail to toggle favorite when datasource throws an error`() = runTest {
        val diary = Diary("Hello World")
        every { dataSource.fetchAll() }.returns(flowOf(listOf(diary)))
        everySuspend { dataSource.update(any()) }.throws(Exception("error toggling favorite"))

        val viewModel = createDashboardViewModel()

        val result = viewModel.toggleFavorite(diary)

        verifySuspend { dataSource.update(any()) }
        assertThat(result).isFalse()
    }

    @Test
    fun `Should transition to content state on dashboard after loading`() = runTest {
        every { dataSource.fetchAll() }.returns(flowOf(emptyList()))
        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            skipItems(1)
            assertThat(awaitItem()).isInstanceOf(DashboardViewModel.DashboardScreenState.Content::class)
        }
    }

    @Test
    fun `Should show error screen when loading diaries throw an error`() = runTest {
        every { dataSource.fetchAll() }.throws(Exception("Error fetching diaries"))
        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            skipItems(1)
            assertThat(awaitItem()).isInstanceOf(DashboardViewModel.DashboardScreenState.Error::class)
        }
    }
}

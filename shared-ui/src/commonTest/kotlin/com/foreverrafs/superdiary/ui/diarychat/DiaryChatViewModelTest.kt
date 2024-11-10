package com.foreverrafs.superdiary.ui.diarychat

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.diaryai.DiaryChatRole
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.GetChatMessagesUseCase
import com.foreverrafs.superdiary.data.usecase.SaveChatMessageUseCase
import com.foreverrafs.superdiary.ui.awaitUntil
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryChatViewModelTest {

    private val dataSource: DataSource = mock<DataSource>()

    private val diaryAI: DiaryAI = mock<DiaryAI>()

    private lateinit var diaryChatViewModel: DiaryChatViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        every { dataSource.fetchAll() }.returns(
            flowOf(
                listOf(
                    Diary(
                        id = 123L,
                        entry = "I am going horse riding today",
                    ),
                ),
            ),
        )
        every { dataSource.getChatMessages() }.returns(flowOf(emptyList()))
        everySuspend { dataSource.saveChatMessage(any()) }.returns(Unit)

        diaryChatViewModel = DiaryChatViewModel(
            diaryAI = diaryAI,
            getAllDiariesUseCase = GetAllDiariesUseCase(
                dataSource = dataSource,
                dispatchers = TestAppDispatchers,
            ),
            logger = AggregateLogger(emptyList()),
            saveChatMessageUseCase = SaveChatMessageUseCase(dataSource = dataSource),
            getChatMessagesUseCase = GetChatMessagesUseCase(dataSource = dataSource),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should update responding to true when generating AI response`() = runTest {
        everySuspend { diaryAI.queryDiaries(any()) }.returns("hello boss")

        diaryChatViewModel.queryDiaries("hello World")

        diaryChatViewModel.viewState.test {
            skipItems(1)
            val state = awaitItem()

            assertThat(state.isResponding).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Should update responding to false after generating AI response`() = runTest {
        everySuspend { diaryAI.queryDiaries(any()) }.returns("hello boss")

        diaryChatViewModel.viewState.test {
            diaryChatViewModel.queryDiaries("hello World")

            // Keep skipping until there is a message generated by AI
            val state =
                awaitUntil { state -> state.messages.none { it.role == DiaryChatRole.DiaryAI } }

            cancelAndConsumeRemainingEvents()

            assertThat(state.isResponding).isFalse()
        }
    }

    @Test
    fun `Should prepend message prompt to messages when querying initially`() = runTest {
        everySuspend {
            diaryAI.queryDiaries(
                any(),
            )
        }.returns("You went horse riding on the 20th of June")

        everySuspend {
            dataSource.saveChatMessage(any())
        }.returns(Unit)

        diaryChatViewModel.queryDiaries("When did I go horse riding?")

        diaryChatViewModel.viewState.test {
            val state = awaitUntil {
                it.messages.any { message -> message.role == DiaryChatRole.System }
            }

            assertThat(state.messages.any { it.role == DiaryChatRole.System }).isTrue()
        }
    }
}

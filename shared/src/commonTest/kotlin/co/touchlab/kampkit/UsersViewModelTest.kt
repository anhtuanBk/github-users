package co.touchlab.kampkit

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import co.touchlab.kampkit.data.UserRepositoryImpl
import co.touchlab.kampkit.db.User
import co.touchlab.kampkit.domain.repository.UserRepository
import co.touchlab.kampkit.domain.usecase.GetUsersUseCase
import co.touchlab.kampkit.mock.UserApiMock
import co.touchlab.kampkit.presentation.UsersViewModel
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class UsersViewModelTest {
    private var kermit = Logger(StaticConfig())
    private var testDbConnection = testDbConnection()
    private var dbHelper = DatabaseHelper(
        testDbConnection,
        kermit,
        Dispatchers.Default
    )

    private val ktorApi = UserApiMock()
    private val repository: UserRepository =
        UserRepositoryImpl(dbHelper, ktorApi, Dispatchers.Default, kermit)
    private val getUsersUseCase: GetUsersUseCase = GetUsersUseCase(repository)

    private val viewModel by lazy {
        UsersViewModel(getUsersUseCase, kermit, Dispatchers.Default)
    }

    private val page: Long = 1

    private val mockDbUsers: List<User> = listOf(
        User(
            "aaaa",
            "mkdc.com",
            "ikdkc.asa",
            page
        ),
        User(
            "dsddd",
            "mksddc.com",
            "ikwewdkc.asa",
            page
        )
    )

    private val mockUsers: List<co.touchlab.kampkit.domain.model.User> = listOf(
        co.touchlab.kampkit.domain.model.User(
            "aaaa",
            "mkdc.com",
            "ikdkc.asa"
        ),
        co.touchlab.kampkit.domain.model.User(
            "dsddd",
            "mksddc.com",
            "ikwewdkc.asa"
        )
    )

    private val mockUsersRemote: List<co.touchlab.kampkit.domain.model.User> = listOf(
        co.touchlab.kampkit.domain.model.User(
            "aaaaa",
            "mkdc.com",
            "ikdkc.asa"
        ),
        co.touchlab.kampkit.domain.model.User(
            "dsdddd",
            "mksddc.com",
            "ikwewdkc.asa"
        )
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        testDbConnection.close()
    }

    @Test
    fun `Fetch Users`() = runTest {
        ktorApi.prepareGetUsersResult(mockUsers)
        viewModel.fetchUsers()

        viewModel.users.test {
            assertEquals(
                mockUsers,
                awaitItemPrecededBy(emptyList())
            )
        }
    }

    @Test
    fun `Fetch Users with cache then plus remote`() = runTest {
        ktorApi.prepareGetUsersResult(mockUsersRemote)
        dbHelper.insertUsers(mockDbUsers)
        val nextItem = mockUsers + mockUsersRemote

        viewModel.fetchUsers()
        viewModel.users.test {
            assertEquals(emptyList(), awaitItem())
            assertEquals(
                nextItem,
                awaitItemPrecededBy(mockUsers)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Fetch Users fail silently and get cache`() = runTest {
        ktorApi.throwGetUsersOnCall(RuntimeException("error"))
        dbHelper.insertUsers(mockDbUsers)

        viewModel.fetchUsers()
        viewModel.users.test {
            assertEquals(
                mockUsers,
                awaitItemPrecededBy(emptyList())
            )
        }
    }

    @Test
    fun `Get users by page empty`() = runTest {
        ktorApi.prepareGetUsersResult(emptyList())

        viewModel.users.test {
            assertEquals(
                emptyList(),
                awaitItem()
            )
        }
    }
}

// There's a race condition where intermediate states can get missed if the next state comes too fast.
// This function addresses that by awaiting an item that may or may not be preceded by the specified other items
private suspend fun <T> ReceiveTurbine<T>.awaitItemPrecededBy(vararg items: T): T {
    var nextItem = awaitItem()
    for (item in items) {
        if (item == nextItem) {
            nextItem = awaitItem()
        }
    }
    return nextItem
}

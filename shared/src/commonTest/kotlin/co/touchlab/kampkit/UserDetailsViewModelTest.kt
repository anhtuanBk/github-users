package co.touchlab.kampkit

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import co.touchlab.kampkit.data.UserRepositoryImpl
import co.touchlab.kampkit.db.UserDetails
import co.touchlab.kampkit.domain.repository.UserRepository
import co.touchlab.kampkit.domain.usecase.GetUserDetailsUseCase
import co.touchlab.kampkit.mock.UserApiMock
import co.touchlab.kampkit.presentation.UserDetailsViewModel
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class UserDetailsViewModelTest {
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
    private val getUserDetailsUseCase: GetUserDetailsUseCase = GetUserDetailsUseCase(repository)

    private val viewModel by lazy {
        UserDetailsViewModel(getUserDetailsUseCase, kermit, Dispatchers.Default)
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    private val login = "cdc"
    private val mockDbUserDetails = UserDetails(
        login,
        "dmck.cjdnc",
        "cjndjc.ncjdnc",
        "jandc",
        1212,
        12313
    )

    private val mockUserDetails = co.touchlab.kampkit.domain.model.UserDetails(
        login,
        "dmck.cjdnc",
        "cjndjc.ncjdnc",
        "jandc",
        1212,
        12313
    )

    private val mockUserDetailsRemote = co.touchlab.kampkit.domain.model.UserDetails(
        login,
        "dmck.cjdncc",
        "cjndjc.ncjdncc",
        "jandcc",
        1212,
        12313
    )

    @BeforeTest
    fun setup() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        testDbConnection.close()
    }

    @Test
    fun `Fetch UserDetails with login`() = runTest {
        ktorApi.prepareGetUserDetailsResult(mockUserDetails)
        scope.launch {
            viewModel.fetchUserDetails(login)
        }
        viewModel.userDetails.test {
            assertEquals(mockUserDetails, awaitItemPrecededBy(null))
        }
    }

    @Test
    fun `Fetch UserDetails with login cache first then remote`() = runTest {
        ktorApi.prepareGetUserDetailsResult(mockUserDetailsRemote)
        dbHelper.insertUserDetail(mockDbUserDetails)
        scope.launch {
            viewModel.fetchUserDetails(login)
        }

        viewModel.userDetails.test {
            assertEquals(mockUserDetails, awaitItemPrecededBy(null))
            assertEquals(mockUserDetailsRemote, awaitItem())
        }
    }

    @Test
    fun `Fail silently on fetch UserDetails error and get cached item`() = runTest {
        ktorApi.throwGetUserDetailsOnCall(RuntimeException("Test error"))
        dbHelper.insertUserDetail(mockDbUserDetails)
        scope.launch {
            viewModel.fetchUserDetails(login)
        }

        viewModel.userDetails.test {
            assertEquals(mockUserDetails, awaitItemPrecededBy(null))
        }
    }
}

private suspend fun <T> ReceiveTurbine<T>.awaitItemPrecededBy(vararg items: T): T {
    var nextItem = awaitItem()
    for (item in items) {
        if (item == nextItem) {
            nextItem = awaitItem()
        }
    }
    return nextItem
}

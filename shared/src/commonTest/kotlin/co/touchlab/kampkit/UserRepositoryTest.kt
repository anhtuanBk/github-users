package co.touchlab.kampkit

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import co.touchlab.kampkit.data.UserRepositoryImpl
import co.touchlab.kampkit.db.User
import co.touchlab.kampkit.db.UserDetails
import co.touchlab.kampkit.domain.repository.UserRepository
import co.touchlab.kampkit.mock.UserApiMock
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRepositoryTest {

    private var kermit = Logger(StaticConfig())
    private var testDbConnection = testDbConnection()
    private var dbHelper = DatabaseHelper(
        testDbConnection,
        kermit,
        Dispatchers.Default
    )
    private val ktorApi = UserApiMock()

    private val repository: UserRepository = UserRepositoryImpl(
        dbHelper,
        ktorApi,
        Dispatchers.Default,
        kermit
    )

    private val page: Long = 1
    private val login = "cdc"

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
            "ikdkc.asa",
        ),
        co.touchlab.kampkit.domain.model.User(
            "dsddd",
            "mksddc.com",
            "ikwewdkc.asa",
        )
    )

    private val mockUsersRemote: List<co.touchlab.kampkit.domain.model.User> = listOf(
        co.touchlab.kampkit.domain.model.User(
            "aaaaa",
            "mkdc.com",
            "ikdkc.asa",
        ),
        co.touchlab.kampkit.domain.model.User(
            "dsdddd",
            "mksddc.com",
            "ikwewdkc.asa",
        )
    )

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

    @AfterTest
    fun tearDown() = runTest {
        testDbConnection.close()
    }

    @Test
    fun `Get Users with page`() = runTest {
        ktorApi.prepareGetUsersResult(mockUsers)
        repository.getUsers(1).test {
            assertEquals(mockUsers, awaitItem())
        }
    }

    @Test
    fun `Get Users with page cache first then plus remote`() = runTest {
        ktorApi.prepareGetUsersResult(mockUsersRemote)
        dbHelper.insertUsers(mockDbUsers)
        val nextItem = mockUsers + mockUsersRemote

        repository.getUsers(page.toInt()).test {
            assertEquals(nextItem, awaitItemPrecededBy(mockUsers))
        }
    }

    @Test
    fun `Get UserDetails with login`() = runTest {
        ktorApi.prepareGetUserDetailsResult(mockUserDetails)
        repository.getUserDetails(login).test {
            assertEquals(mockUserDetails, awaitItem())
        }
    }

    @Test
    fun `Get UserDetails with login cache first then plus remote`() = runTest {
        ktorApi.prepareGetUserDetailsResult(mockUserDetailsRemote)
        dbHelper.insertUserDetail(mockDbUserDetails)

        repository.getUserDetails(login).test {
            assertEquals(mockUserDetailsRemote, awaitItemPrecededBy(mockUserDetails))
        }
    }

    @Test
    fun `Fail silently on fetch Users error and get cached item`() = runTest {
        ktorApi.throwGetUsersOnCall(RuntimeException("Test error"))
        dbHelper.insertUsers(mockDbUsers)

        repository.getUsers(page.toInt()).test {
            assertEquals(mockUsers, awaitItem())
        }
    }

    @Test
    fun `Fail silently on fetch UserDetails error and get cached item`() = runTest {
        ktorApi.throwGetUserDetailsOnCall(RuntimeException("Test error"))
        dbHelper.insertUserDetail(mockDbUserDetails)

        repository.getUserDetails(login).test {
            assertEquals(mockUserDetails, awaitItem())
        }
    }
}


// There's a race condition where intermediate states can get missed if the next state comes too fast.
// This function addresses that by awaiting an item that may or may not be preceded by the specified other items
private suspend fun <T> ReceiveTurbine<T>.awaitItemPrecededBy(
    vararg items: T
): T {
    var nextItem = awaitItem()
    for (item in items) {
        if (item == nextItem) {
            nextItem = awaitItem()
        }
    }
    return nextItem
}
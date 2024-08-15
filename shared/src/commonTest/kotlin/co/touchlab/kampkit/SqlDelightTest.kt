package co.touchlab.kampkit

import co.touchlab.kampkit.db.User
import co.touchlab.kampkit.db.UserDetails
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlDelightTest {

    private lateinit var dbHelper: DatabaseHelper

    private suspend fun DatabaseHelper.insertUsers(users: List<User>) {
        insertUsers(users)
    }

    private val page: Long = 1
    private val login = "cdc"

    private val mockUsers: List<User> = listOf(
        User(
            "aaa",
            "mkdc.com",
            "ikdkc.asa",
            page
        ),
        User(
            "dsdd",
            "mksddc.com",
            "ikwewdkc.asa",
            page
        )
    )

    private val mockUserDetails = UserDetails(
        login,
        "dmck.cjdnc",
        "cjndjc.ncjdnc",
        "jandc",
        1212,
        12313
    )

    @BeforeTest
    fun setup() = runTest {
        dbHelper = DatabaseHelper(
            testDbConnection(),
            Logger(StaticConfig()),
            Dispatchers.Default
        )
        dbHelper.deleteAll()
        dbHelper.insertUsers(mockUsers)
        dbHelper.insertUserDetail(mockUserDetails)
    }

    @Test
    fun `Select Users by page Success`() = runTest {
        val users = dbHelper.selectUsersByPage(page.toInt()).first()
        assertEquals(
            users,
            mockUsers
        )
    }

    @Test
    fun `Select UserDetails by login Success`() = runTest {
        val userDetails = dbHelper.selectUserDetailsByLogin(login).first()
        assertEquals(
            userDetails,
            mockUserDetails
        )
    }

    @Test
    fun `Delete All Success`() = runTest {
        assertTrue(dbHelper.selectUsersByPage(page.toInt()).first().isNotEmpty())
        dbHelper.deleteAll()

        assertTrue(
            dbHelper.selectUsersByPage(page.toInt()).first().isEmpty(),
            "Delete All did not work"
        )
    }
}

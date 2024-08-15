package co.touchlab.kampkit

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneNotNull
import app.cash.sqldelight.db.SqlDriver
import co.touchlab.kampkit.db.User
import co.touchlab.kampkit.db.KaMPKitDb
import co.touchlab.kampkit.db.UserDetails
import co.touchlab.kampkit.sqldelight.transactionWithContext
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onErrorResume
import kotlinx.coroutines.flow.onErrorResumeNext

class DatabaseHelper(
    sqlDriver: SqlDriver,
    private val log: Logger,
    private val backgroundDispatcher: CoroutineDispatcher
) {
    private val dbRef: KaMPKitDb = KaMPKitDb(sqlDriver)

    fun selectUsersByPage(page: Int): Flow<List<User>> = dbRef.tableQueries
        .selectUsersByPage(page.toLong())
        .asFlow()
        .mapToList(backgroundDispatcher)
        .flowOn(backgroundDispatcher)

    suspend fun insertUsers(users: List<User>) {
        log.d { "Inserting ${users.size} users into database" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            users.forEach { user ->
                dbRef.tableQueries
                    .insertUser(
                        login = user.login,
                        avatarUrl = user.avatarUrl,
                        htmlUrl = user.htmlUrl,
                        page = user.page)
            }
        }
    }

    fun selectUserDetailsByLogin(login: String): Flow<UserDetails> = dbRef.tableQueries
        .selectUserDetailsByLogin(login)
        .asFlow()
        .mapToOneNotNull(backgroundDispatcher)
        .flowOn(backgroundDispatcher)

    suspend fun insertUserDetail(userDetails: UserDetails) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries
                .insertUserDetails(
                    userDetails.login,
                    userDetails.avatarUrl,
                    userDetails.htmlUrl,
                    userDetails.location,
                    userDetails.followers,
                    userDetails.following
                )
        }
    }

    suspend fun deleteAll() {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.deleteAllUsers()
            dbRef.tableQueries.deleteAllUserDetails()
        }
    }
}

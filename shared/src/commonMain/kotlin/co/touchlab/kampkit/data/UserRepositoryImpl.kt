package co.touchlab.kampkit.data

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.data.remote.UserApi
import co.touchlab.kampkit.domain.model.User
import co.touchlab.kampkit.domain.model.UserDetails
import co.touchlab.kampkit.domain.repository.UserRepository
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class UserRepositoryImpl(
  private val dbHelper: DatabaseHelper,
  private val userApi: UserApi,
  private val dispatcher: CoroutineDispatcher,
  log: Logger,
): UserRepository {

  private val log = log.withTag("UserModel")

  override fun getUsers(page: Int): Flow<List<User>> {
      CoroutineScope(dispatcher).launch {
        try {
          val users = userApi.getUsers(page)
          dbHelper.insertUsers(users
            .map { co.touchlab.kampkit.db.User(
              login = it.login,
              avatarUrl = it.avatarUrl,
              htmlUrl = it.htmlUrl,
              page = page.toLong()
            ) })
        } catch (e: Exception) {
          log.e(messageString = e.message.orEmpty(), e)
        }
      }
      return dbHelper.selectUsersByPage(page)
        .map { it
          .map { user -> User(
            login = user.login,
            avatarUrl = user.avatarUrl,
            htmlUrl = user.htmlUrl
          ) } }
  }

  override fun getUserDetails(login: String): Flow<UserDetails> {
      CoroutineScope(dispatcher).launch {
        try {
          val userDetails = userApi.getUserDetails(login)
          dbHelper.insertUserDetail(co.touchlab.kampkit.db.UserDetails(
            userDetails.login,
            userDetails.avatarUrl,
            userDetails.htmlUrl,
            userDetails.location,
            userDetails.followers.toLong(),
            userDetails.following.toLong()
          ))
        } catch (e: Exception) {
          log.e(e.message.orEmpty(), e)
        }
      }
      return dbHelper.selectUserDetailsByLogin(login)
        .map { UserDetails(
          it.login,
          it.avatarUrl,
          it.htmlUrl,
          it.location.orEmpty(),
          it.followers.toInt(),
          it.following.toInt()
        ) }
  }
}

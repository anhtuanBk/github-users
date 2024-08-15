package co.touchlab.kampkit.mock

import co.touchlab.kampkit.data.remote.UserApi
import co.touchlab.kampkit.domain.model.User
import co.touchlab.kampkit.domain.model.UserDetails

class UserApiMock : UserApi {
    private var nextUsersResult: () -> List<User> = { error("Uninitialized!") }
    private var nextUserDetailsResult: () -> UserDetails = { error("Uninitialized!") }
    var getUsersCalledCount = 0
        private set
    var getUserDetailsCalledCount = 0
        private  set

    override suspend fun getUsers(page: Int, perPage: Int): List<User> {
        val result = nextUsersResult()
        getUsersCalledCount++
        return result
    }

    override suspend fun getUserDetails(login: String): UserDetails {
        val result = nextUserDetailsResult()
        getUserDetailsCalledCount++
        return result
    }

    fun prepareGetUsersResult(result: List<User>) {
        nextUsersResult = { result }
    }

    fun throwGetUsersOnCall(throwable: Throwable) {
        nextUsersResult = { throw throwable }
    }

    fun prepareGetUserDetailsResult(result: UserDetails) {
        nextUserDetailsResult = { result }
    }

    fun throwGetUserDetailsOnCall(throwable: Throwable) {
        nextUserDetailsResult = { throw throwable }
    }
}

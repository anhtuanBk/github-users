package co.touchlab.kampkit.data.remote

import co.touchlab.kampkit.domain.model.User
import co.touchlab.kampkit.domain.model.UserDetails

interface UserApi {
    suspend fun getUsers(page: Int, perPage: Int = 20): List<User>
    suspend fun getUserDetails(login: String): UserDetails
}

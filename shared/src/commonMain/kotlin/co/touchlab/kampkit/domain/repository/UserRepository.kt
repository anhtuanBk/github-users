package co.touchlab.kampkit.domain.repository

import co.touchlab.kampkit.domain.model.User
import co.touchlab.kampkit.domain.model.UserDetails
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(page: Int): Flow<List<User>>
    fun getUserDetails(login: String): Flow<UserDetails>
}

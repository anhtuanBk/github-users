package co.touchlab.kampkit.domain.usecase

import co.touchlab.kampkit.domain.model.User
import co.touchlab.kampkit.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(private val userRepository: UserRepository) {
    fun execute(page: Int): Flow<List<User>> = userRepository.getUsers(page)
}

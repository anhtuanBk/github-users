package co.touchlab.kampkit.domain.usecase

import co.touchlab.kampkit.domain.model.UserDetails
import co.touchlab.kampkit.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserDetailsUseCase(private val userRepository: UserRepository) {
  fun execute(login: String): Flow<UserDetails> = userRepository.getUserDetails(login)
}

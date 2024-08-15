package co.touchlab.kampkit.presentation

import co.touchlab.kampkit.domain.model.UserDetails
import co.touchlab.kampkit.domain.usecase.GetUserDetailsUseCase
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class UserDetailsViewModel(
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val logger: Logger,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _userDetails = MutableStateFlow<UserDetails?>(null)
    val userDetails: StateFlow<UserDetails?> = _userDetails.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    suspend fun fetchUserDetails(login: String) {
        _isLoading.value = true
            getUserDetailsUseCase.execute(login)
                .onCompletion {
                    _isLoading.value = false
                }
                .catch {
                    _isLoading.value = false
                    logger.e(it.message.orEmpty(), it)
                    handleException(it)
                }
                .filterNotNull()
                .collect { userDetails ->
                    _userDetails.value = userDetails
                    _isLoading.value = false
                }
    }

    private fun handleException(exception: Throwable) {
        logger.e(exception.message.orEmpty(), exception)
        _errorMessage.value = "An unexpected error occurred. " + exception.message
    }
}
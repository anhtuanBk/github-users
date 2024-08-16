package co.touchlab.kampkit.presentation

import co.touchlab.kampkit.domain.model.User
import co.touchlab.kampkit.domain.usecase.GetUsersUseCase
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class UsersViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val logger: Logger,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPage = 1

    fun fetchUsers() {
        if (_isLoading.value) return // Prevent multiple requests at once
        _isLoading.value = true
        CoroutineScope(dispatcher).launch {
            getUsersUseCase.execute(currentPage)
                .onCompletion {
                    _isLoading.value = false
                }
                .catch {
                    _isLoading.value = false
                    logger.e(it.message.orEmpty(), it)
                    handleException(it)
                }
                .collect { newUsers ->
                    if (newUsers.isEmpty()) return@collect
                    _users.value += newUsers
                    currentPage++
                    _isLoading.value = false
                }
        }
    }

    private fun handleException(exception: Throwable) {
        logger.e(exception.message.orEmpty(), exception)
        _errorMessage.value = "An unexpected error occurred. " + exception.message
    }
}

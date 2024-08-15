package co.touchlab.kampkit.android.ui.users

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.presentation.UsersViewModel
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    usersViewModel: UsersViewModel,
    onShowUserDetails: (String) -> Unit
) {
    val users by usersViewModel.users.collectAsStateWithLifecycle()
    val errorMessage by usersViewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by usersViewModel.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                UsersList(
                    items = users.toImmutableList(),
                    isLoading = isLoading,
                    error = null,
                    hasReachedMax = false,
                    onRetry = {},
                    onLoadNextPage = {
                        usersViewModel.fetchUsers()
                    },
                    onItemClick = {
                        onShowUserDetails(it.login)
                    }
                )
            }
        }
    }
}
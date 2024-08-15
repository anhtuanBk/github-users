package co.touchlab.kampkit.android.ui.users

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import co.touchlab.kampkit.platformModule
import co.touchlab.kampkit.presentation.UsersViewModel
import kotlinx.serialization.Serializable
import org.koin.core.Koin
import org.koin.core.component.KoinComponent

@Serializable
internal data object UsersRoute

internal fun NavGraphBuilder.usersScreen(
    viewModel: UsersViewModel,
    onShowUserDetails: (String) -> Unit
) {
    composable<UsersRoute> {
        UsersScreen(
            viewModel,
            onShowUserDetails
        )
    }
}

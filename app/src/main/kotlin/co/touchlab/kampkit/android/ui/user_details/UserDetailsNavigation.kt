package co.touchlab.kampkit.android.ui.user_details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import co.touchlab.kampkit.presentation.UserDetailsViewModel
import kotlinx.serialization.Serializable

@Serializable
internal data class UserDetailsRoute(val login: String)

internal fun NavGraphBuilder.userDetailsScreen(
    viewModel: UserDetailsViewModel,
    onBack: () -> Unit
) {
    composable<UserDetailsRoute> {
        val (login) = it.toRoute<UserDetailsRoute>()
        UserDetailsScreen(
            viewModel = viewModel,
            login = login
        ) {
            onBack()
        }
    }
}

package co.touchlab.kampkit.android.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import co.touchlab.kampkit.android.ui.user_details.UserDetailsRoute
import co.touchlab.kampkit.android.ui.user_details.userDetailsScreen
import co.touchlab.kampkit.android.ui.users.UsersRoute
import co.touchlab.kampkit.android.ui.users.usersScreen
import co.touchlab.kampkit.presentation.UserDetailsViewModel
import co.touchlab.kampkit.presentation.UsersViewModel
import kotlinx.serialization.Serializable

@Serializable
object MainGraph

fun NavGraphBuilder.mainGraph(
    navController: NavController,
    usersViewModel: UsersViewModel,
    userDetailsViewModel: UserDetailsViewModel
) {
    navigation<MainGraph>(startDestination = UsersRoute) {
        usersScreen(
            usersViewModel
        ) {
            navController.navigate(UserDetailsRoute(it))
        }

        userDetailsScreen(
            userDetailsViewModel
        ) {
            navController.navigateUp()
        }
    }
}

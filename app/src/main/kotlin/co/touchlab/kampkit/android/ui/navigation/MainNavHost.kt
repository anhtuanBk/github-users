package co.touchlab.kampkit.android.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import co.touchlab.kampkit.presentation.UserDetailsViewModel
import co.touchlab.kampkit.presentation.UsersViewModel

@Composable
internal fun MainNavHost(
    usersViewModel: UsersViewModel,
    userDetailsViewModel: UserDetailsViewModel
) {
    val navController = rememberNavController()

    Column {
        NavHost(
            navController = navController,
            startDestination = MainGraph,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .weight(1f)
        ) {
            mainGraph(
                navController = navController,
                usersViewModel = usersViewModel,
                userDetailsViewModel = userDetailsViewModel
            )
        }
    }
}

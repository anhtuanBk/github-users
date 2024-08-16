package co.touchlab.kampkit.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.touchlab.kampkit.android.ui.coreUI.AppBackground
import co.touchlab.kampkit.android.ui.coreUI.AppTheme
import co.touchlab.kampkit.android.ui.navigation.MainNavHost
import co.touchlab.kampkit.presentation.UserDetailsViewModel
import co.touchlab.kampkit.presentation.UsersViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class MainActivity :
    ComponentActivity(),
    KoinComponent {
    private val usersViewModel: UsersViewModel by viewModel()
    private val userDetailsViewModel: UserDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppBackground {
                    MainNavHost(
                        usersViewModel,
                        userDetailsViewModel
                    )
                }
            }
        }
    }
}

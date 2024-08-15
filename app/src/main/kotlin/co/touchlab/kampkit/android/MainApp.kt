package co.touchlab.kampkit.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import co.touchlab.kampkit.AppInfo
import co.touchlab.kampkit.initKoin
import co.touchlab.kampkit.presentation.UserDetailsViewModel
import co.touchlab.kampkit.presentation.UsersViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }
                viewModel {
                    UsersViewModel(
                        get(),
                        get { parametersOf("UsersViewModel") },
                        Dispatchers.Default
                    )
                }
                viewModel {
                    UserDetailsViewModel(
                        get(),
                        get { parametersOf("UserDetailsViewModel") },
                        Dispatchers.Default
                    )
                }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences(
                        "KAMPSTARTER_SETTINGS",
                        Context.MODE_PRIVATE
                    )
                }
                single<AppInfo> { AndroidAppInfo }
                single {
                    { Log.i("Startup", "Hello from Android/Kotlin!") }
                }
            }
        )
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}

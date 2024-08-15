package co.touchlab.kampkit

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.kampkit.db.KaMPKitDb
import co.touchlab.kampkit.presentation.UserDetailsViewModel
import co.touchlab.kampkit.presentation.UsersViewModel
import co.touchlab.kermit.Logger
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

fun initKoinIos(
    userDefaults: NSUserDefaults,
    appInfo: AppInfo,
    doOnStartup: () -> Unit
): KoinApplication = initKoin(
    module {
        single<Settings> { NSUserDefaultsSettings(userDefaults) }
        single { appInfo }
        single { doOnStartup }
    }
)

actual val platformModule = module {
    single<SqlDriver> { NativeSqliteDriver(KaMPKitDb.Schema, "KampkitDb") }

    single { Darwin.create() }

    single {
        UsersViewModel(
            get(),
            getWith("UsersViewModel"),
            Dispatchers.Default
    ) }

    single {
        UserDetailsViewModel(
            get(),
            getWith("UserDetailsViewModel"),
            Dispatchers.Default
        )
    }
}

// Access from Swift to create a logger
@Suppress("unused")
fun Koin.loggerWithTag(tag: String) = get<Logger>(qualifier = null) { parametersOf(tag) }

@Suppress("unused") // Called from Swift
object KotlinDependencies : KoinComponent {
    fun getUsersViewModel() = getKoin().get<UsersViewModel>()
    fun getUserDetailsViewModel() = getKoin().get<UserDetailsViewModel>()
}

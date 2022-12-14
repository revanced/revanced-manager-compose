package app.revanced.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.revanced.manager.domain.manager.PreferencesManager
import app.revanced.manager.ui.component.PermissionsDialog
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.navigation.DashboardDestination
import app.revanced.manager.ui.navigation.back
import app.revanced.manager.ui.screen.DashboardScreen
import app.revanced.manager.ui.screen.MainDashboardScreen
import app.revanced.manager.ui.screen.PatcherScreen
import app.revanced.manager.ui.screen.SettingsScreen
import app.revanced.manager.ui.screen.subscreens.*
import app.revanced.manager.ui.theme.ReVancedManagerTheme
import app.revanced.manager.ui.theme.Theme
import dev.olshevski.navigation.reimagined.*
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid
import org.koin.android.ext.android.inject
import java.util.concurrent.CancellationException

class MainActivity : ComponentActivity() {
    private val prefs: PreferencesManager by inject()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        SentryAndroid.init(this) {
            it.dsn = if (prefs.sentry) BuildConfig.SENTRY_DSN else ""
            it.environment = BuildConfig.BUILD_TYPE
            it.release = BuildConfig.VERSION_NAME
            it.addIgnoredExceptionForType(CancellationException::class.java)

            it.beforeSend = SentryOptions.BeforeSendCallback { event, _ ->
                if (prefs.sentry) {
                    event
                } else null
            }
        }
        super.onCreate(savedInstanceState)

        setContent {
            ReVancedManagerTheme(
                dynamicColor = prefs.dynamicColor,
                darkTheme = prefs.theme == Theme.SYSTEM && isSystemInDarkTheme() || prefs.theme == Theme.DARK,
            ) {
                val navController = rememberNavController<AppDestination>(AppDestination.Dashboard)

                NavBackHandler(navController)

                PermissionsDialog()

                AnimatedNavHost(
                    controller = navController,
                    transitionSpec = { _, _, _ -> fadeIn() with fadeOut() }
                ) {
                    when (val destination = this.currentHostEntry.destination) {
                        is DashboardDestination -> MainDashboardScreen(
                            currentDestination = destination,
                            bottomNavItems = listOf(
                                AppDestination.Dashboard,
                                AppDestination.Patcher,
                                AppDestination.Settings
                            ),
                            onNavChanged = { navController.replaceLast(it) }
                        ) {
                            when (destination) {
                                AppDestination.Dashboard -> DashboardScreen()
                                AppDestination.Patcher -> PatcherScreen(
                                    onClickAppSelector = { navController.navigate(AppDestination.AppSelector) },
                                    onClickPatchSelector = { navController.navigate(AppDestination.PatchSelector) },
                                    onClickPatch = { navController.navigate(AppDestination.Patching) },
                                    onClickSourceSelector = { navController.navigate(AppDestination.SourceSelector) }
                                )
                                AppDestination.Settings -> SettingsScreen(
                                    onClickContributors = { navController.navigate(AppDestination.Contributors) },
                                    onClickLicenses = { navController.navigate(AppDestination.Licenses) }
                                )
                            }
                        }
                        is AppDestination.AppSelector -> AppSelectorSubscreen(onBackClick = { navController.back() })
                        is AppDestination.PatchSelector -> PatchesSelectorSubscreen(onBackClick = { navController.back() })
                        is AppDestination.Contributors -> ContributorsSubscreen(onBackClick = { navController.back() })
                        is AppDestination.SourceSelector -> SourceSelectorSubscreen(onBackClick = { navController.back() })
                        is AppDestination.Licenses -> LicensesSubscreen(onBackClick = { navController.back() })
                        is AppDestination.Patching -> PatchingSubscreen(onBackClick = { navController.back() })
                    }
                }
            }
        }
    }
}
package hello.kwfriends.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.base.BaseActivity
import hello.kwfriends.ui.screens.auth.AuthScreen
import hello.kwfriends.ui.screens.home.HomeScreen
import hello.kwfriends.ui.screens.home.HomeViewModel
import hello.kwfriends.ui.screens.newPost.NewPostScreen
import hello.kwfriends.ui.screens.newPost.NewPostViewModel
import hello.kwfriends.ui.screens.settings.SettingsScreen
import hello.kwfriends.ui.screens.settings.SettingsViewModel
import hello.kwfriends.ui.theme.KWFriendsTheme

class MainActivity : BaseActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KWFriendsTheme {
                val navController = rememberNavController()
                val startPoint = intent.getStringExtra("startPoint")
                val startDestination = if(startPoint == "auth") Routes.AUTH_SCREEN else Routes.HOME_SCREEN

                NavHost(navController = navController, startDestination = startDestination) {
                    composable(Routes.HOME_SCREEN) {
                        HomeScreen(
                            homeViewModel = homeViewModel,
                            settingsViewModel = settingsViewModel,
                            navigation = navController
                        )
                    }
                    composable(Routes.SETTINGS_SCREEN) {
                        SettingsScreen(
                            settingsViewModel= settingsViewModel,
                            navigation = navController
                        )
                    }
                    composable(Routes.AUTH_SCREEN) {
                        AuthScreen(navigation = navController)
                    }
                    composable(Routes.NEW_POST_SCREEN) {
                        NewPostScreen(
                            homeViewModel = homeViewModel,
                            postViewModel = NewPostViewModel(),
                            navigation = navController
                        )
                    }
                }
            }
        }
    }

}
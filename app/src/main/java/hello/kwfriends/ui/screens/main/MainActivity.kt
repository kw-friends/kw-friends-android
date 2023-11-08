package hello.kwfriends.ui.screens.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.base.BaseActivity
import hello.kwfriends.ui.screens.auth.AuthScreen
import hello.kwfriends.ui.screens.auth.AuthViewModel
import hello.kwfriends.ui.screens.post.NewPostScreen
import hello.kwfriends.ui.screens.post.NewPostViewModel
import hello.kwfriends.ui.screens.settings.SettingsScreen
import hello.kwfriends.ui.theme.KWFriendsTheme

class MainActivity : BaseActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KWFriendsTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Routes.AUTH_SCREEN) {
                    composable(Routes.HOME_SCREEN) {
                        MainScreen(
                            mainViewModel = mainViewModel,
                            authViewModel = authViewModel,
                            navigation = navController
                        )
                    }
                    composable(Routes.SETTINGS_SCREEN) {
                        SettingsScreen(
                            authViewModel = authViewModel,
                            navigation = navController
                        )
                    }
                    composable(Routes.AUTH_SCREEN) {
                        AuthScreen(viewModel = authViewModel, navigation = navController)
                    }
                    composable(Routes.NEW_POST_SCREEN) {
                        NewPostScreen(
                            mainViewModel = mainViewModel,
                            postViewModel = NewPostViewModel(),
                            navigation = navController
                        )
                    }
                }
            }
        }
    }

}
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
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KWFriendsTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Routes.HOME_SCREEN) {
                    composable(Routes.HOME_SCREEN) {
                        MainScreen(viewModel = viewModel, navigation = navController)
                    }
                    composable(Routes.SETTINGS_SCREEN) {
                        SettingsScreen(navigation = navController)
                    }
                    composable(Routes.AUTH_SCREEN) {
                        AuthScreen(viewModel = AuthViewModel(), navigation = navController)
                    }
                    composable(Routes.NEW_POST_SCREEN) {
                        NewPostScreen(
                            mainViewModel = viewModel,
                            postViewModel = NewPostViewModel(),
                            navigation = navController
                        )
                    }
                }
            }

        }
    }

}
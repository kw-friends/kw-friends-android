package hello.kwfriends.ui.screens.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.base.BaseActivity
import hello.kwfriends.ui.screens.auth.AuthScreen
import hello.kwfriends.ui.screens.post.NewPostScreen
import hello.kwfriends.ui.screens.post.NewPostViewModel
import hello.kwfriends.ui.screens.settings.SettingsScreen
import hello.kwfriends.ui.theme.KWFriendsTheme

class MainActivity : BaseActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KWFriendsTheme {
                val navController = rememberNavController()
                val startPoint = intent.getStringExtra("startPoint")
                val startDestination = if(startPoint == "auth") Routes.AUTH_SCREEN else Routes.HOME_SCREEN

                NavHost(navController = navController, startDestination = startDestination) {
                    composable(Routes.HOME_SCREEN) {
                        MainScreen(
                            mainViewModel = mainViewModel,
                            navigation = navController
                        )
                    }
                    composable(Routes.SETTINGS_SCREEN) {
                        SettingsScreen(navigation = navController)
                    }
                    composable(Routes.AUTH_SCREEN) {
                        AuthScreen(navigation = navController)
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
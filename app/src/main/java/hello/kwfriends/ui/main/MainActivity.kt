package hello.kwfriends.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import hello.kwfriends.ui.base.BaseActivity
import hello.kwfriends.ui.screens.auth.AuthScreen
import hello.kwfriends.ui.screens.home.HomeScreen
import hello.kwfriends.ui.screens.home.HomeViewModel
import hello.kwfriends.ui.screens.newPost.NewPostViewModel
import hello.kwfriends.ui.screens.settings.SettingsScreen
import hello.kwfriends.ui.screens.settings.SettingsViewModel
import hello.kwfriends.ui.theme.KWFriendsTheme

class MainActivity : BaseActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val newPostViewModel: NewPostViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KWFriendsTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight
                val statusBarColor = remember { mutableStateOf(Color(0xFFFFFBFF)) }

                LaunchedEffect(statusBarColor.value) {
                    systemUiController.setStatusBarColor(
                        color = statusBarColor.value, // 여기에 원하는 색상을 설정하세요
                        darkIcons = useDarkIcons
                    )
                }

                val navController = rememberNavController()
                val startPoint = intent.getStringExtra("startPoint")
                val startDestination = if(startPoint == "auth") Routes.AUTH_SCREEN else Routes.HOME_SCREEN

                NavHost(navController = navController, startDestination = startDestination) {
                    composable(Routes.HOME_SCREEN) {
                        statusBarColor.value = Color(0xFFFFFBFF)
                        HomeScreen(
                            homeViewModel = homeViewModel,
                            newPostViewModel = newPostViewModel,
                            settingsViewModel = settingsViewModel,
                            navigation = navController
                        )
                    }
                    composable(Routes.SETTINGS_SCREEN) {
                        statusBarColor.value = Color(0xFFE2A39B)
                        SettingsScreen(
                            settingsViewModel= settingsViewModel,
                            navigation = navController
                        )
                    }
                    composable(Routes.AUTH_SCREEN) {
                        statusBarColor.value = Color(0xFFE79898)
                        AuthScreen(navigation = navController)
                    }
                }
            }
        }
    }

}
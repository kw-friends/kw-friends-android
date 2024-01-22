package hello.kwfriends.ui.screens.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import hello.kwfriends.ui.screens.chatting.ChattingScreen
import hello.kwfriends.ui.screens.chatting.ChattingViewModel
import hello.kwfriends.ui.screens.chattingList.ChattingLIstViewModel
import hello.kwfriends.ui.screens.chattingList.ChattingListScreen
import hello.kwfriends.ui.screens.post.setPostData.SetPostDataViewModel
import hello.kwfriends.ui.screens.settings.SettingsScreen
import hello.kwfriends.ui.screens.settings.SettingsViewModel
import hello.kwfriends.ui.theme.KWFriendsTheme

class MainActivity : BaseActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val setPostDataViewModel: SetPostDataViewModel by viewModels()
    private val chattingLIstViewModel: ChattingLIstViewModel by viewModels()
    private val chattingViewModel: ChattingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KWFriendsTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight
                val statusBarColor = remember { mutableStateOf(Color(0xFFFFFBFF)) }

                //상태바 색상 설정
                LaunchedEffect(statusBarColor.value) {
                    systemUiController.setStatusBarColor(
                        color = statusBarColor.value,
                        darkIcons = useDarkIcons
                    )
                }

                val navController = rememberNavController()
                val startPoint = intent.getStringExtra("startPoint")
                val startDestination =
                    if (startPoint == "auth") Routes.AUTH_SCREEN else Routes.HOME_SCREEN

                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    composable(Routes.HOME_SCREEN) {
                        statusBarColor.value = Color(0xFFFFFBFF)
                        MainScreen(
                            mainViewModel = mainViewModel,
                            settingsViewModel = settingsViewModel,
                            setPostDataViewModel = setPostDataViewModel,
                            mainNavigation = navController
                        )
                    }
                    composable(Routes.SETTINGS_SCREEN) {
                        statusBarColor.value = Color(0xFFFFFBFF)
                        SettingsScreen(
                            settingsViewModel = settingsViewModel,
                            mainNavigation = navController
                        )
                    }
                    composable(Routes.AUTH_SCREEN) {
                        statusBarColor.value = Color(0xFFE79898)
                        AuthScreen(mainNavigation = navController)
                    }
                    composable(Routes.CHATTING_LIST_SCREEN) {
                        statusBarColor.value = Color(0xFFFFFBFF)
                        ChattingListScreen(
                            chattingLIstViewModel = chattingLIstViewModel,
                            navigation = navController
                        )
                    }
                    composable(route = Routes.CHATTING_SCREEN + "/{roomID}") { navBackStackEntry ->
                        statusBarColor.value = Color(0xFFFFFBFF)
                        ChattingScreen(
                            chattingViewModel = chattingViewModel,
                            navigation = navController,
                            roomID = navBackStackEntry.arguments?.getString("roomID")!!
                        )
                    }
                }
            }
        }
    }

}
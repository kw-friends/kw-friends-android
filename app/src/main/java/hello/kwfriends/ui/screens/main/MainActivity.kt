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
import hello.kwfriends.ui.screens.newPost.NewPostScreen
import hello.kwfriends.ui.screens.newPost.NewPostViewModel
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
                var startNav = Routes.HOME_SCREEN

                //인증 과정 안되어있으면 인증 화면으로 이동
                //            if (
                //                Firebase.auth.currentUser == null //로그인 유무 검사
                //                || Firebase.auth.currentUser?.isEmailVerified != true //이메일 인증 유무 검사
                ////        || !authViewModel.userAuthChecked //인증 갱신 및 유효성 유무 검사
                //                || !authViewModel.userInputChecked //유저 정보 입력 유무 검사
                //            ) { // 로그인 된 상태일 때
                //                Log.w("Lim", "인증 화면으로 이동!")
                //                Log.w("Lim", "${authViewModel.userInputChecked}")
                //                //startNav = Routes.AUTH_SCREEN
                //            }
                //            else{
                //                Log.w("Lim", "${authViewModel.userInputChecked}")
                //            }

                NavHost(navController = navController, startDestination = startNav) {
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
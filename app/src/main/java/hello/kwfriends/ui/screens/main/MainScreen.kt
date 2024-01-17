@file:OptIn(ExperimentalMaterialApi::class)

package hello.kwfriends.ui.screens.main

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.firebase.realtimeDatabase.Action
import hello.kwfriends.ui.component.HomeBottomBar
import hello.kwfriends.ui.component.MainTopAppBar
import hello.kwfriends.ui.screens.NotChatScreen
import hello.kwfriends.ui.screens.findGathering.FindGatheringScreen
import hello.kwfriends.ui.screens.home.HomeScreen
import hello.kwfriends.ui.screens.post.setPostData.SetPostDataViewModel
import hello.kwfriends.ui.screens.settings.SettingsViewModel
import hello.kwfriends.ui.theme.KWFriendsTheme


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    setPostDataViewModel: SetPostDataViewModel,
    settingsViewModel: SettingsViewModel,
    mainNavigation: NavController
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val homeNavigation = rememberMainNavigation(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    //유저 개인 설정 세팅값 받아오기
    if (!settingsViewModel.userSettingValuesLoaded) {
        settingsViewModel.userSettingValuesLoaded = true
        settingsViewModel.userSettingValuesLoad()
    }

    //검색창에 대한 포커스
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(!mainViewModel.isSearching) {
        focusRequester.requestFocus()
    }

    //두번눌러서 앱 종료에 필요한 코드
    var backPressedTime = 0L
    val startMain = remember { Intent(Intent.ACTION_MAIN) }
    startMain.addCategory(Intent.CATEGORY_HOME)
    startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    //뒤로 가기 버튼을 눌렀을 때 실행할 코드
    BackHandler {
        //검색 취소
        if (mainViewModel.isSearching) {
            mainViewModel.isSearching = false
        }
        //두번눌러서 앱 종료
        else {
            if (System.currentTimeMillis() - backPressedTime <= 2000L) {
                context.startActivity(startMain) // 앱 종료
            } else {
                Toast.makeText(context, "한 번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
            }
            backPressedTime = System.currentTimeMillis()
        }
    }

    Scaffold(
        //앱 바
        topBar = {
            MainTopAppBar(
                navigation = mainNavigation,
                isSearching = mainViewModel.isSearching,
                searchText = mainViewModel.searchText,
                setSearchText = { mainViewModel.setSearchContentText(it) },
                clickSearchButton = { mainViewModel.onclickSearchButton() },
                clickBackButton = { mainViewModel.isSearching = false },
                focusRequester = focusRequester,
            )
        },
        //플로팅 버튼
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "모임 생성", style = MaterialTheme.typography.bodyMedium) },
                icon = { Icon(Icons.Default.Add, "모임 생성") },
                onClick = {
                    mainViewModel.setPostDataState = Action.ADD to ""
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        //네비게이션 바
        bottomBar = { // later: HorizontalPager 사용할것
            HomeBottomBar(
                currentDestination = currentDestination,
                onNavigate = { homeNavigation.navigateTo(it) })
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = MainDestination.HomeScreen.route,
            Modifier.padding(paddingValues)
        ) {
            composable(MainDestination.FindGatheringScreen.route) {
                FindGatheringScreen(
                    mainViewModel = mainViewModel,
                    setPostDataViewModel = setPostDataViewModel,
                    posts = mainViewModel.posts
                )
            }
            composable(MainDestination.HomeScreen.route) {
                HomeScreen()
            }
            composable(MainDestination.ChatScreen.route) {
                NotChatScreen()
            }

        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    KWFriendsTheme {
        MainScreen(
            mainViewModel = MainViewModel(),
            settingsViewModel = SettingsViewModel(),
            setPostDataViewModel = SetPostDataViewModel(),
            mainNavigation = navController
        )
    }
}
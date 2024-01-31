package hello.kwfriends.ui.screens.main

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import hello.kwfriends.ui.component.EnjoyButton
import hello.kwfriends.ui.component.HomeBottomBar
import hello.kwfriends.ui.component.MainTopAppBar
import hello.kwfriends.ui.component.PostReportDialog
import hello.kwfriends.ui.component.UserInfoPopup
import hello.kwfriends.ui.component.UserReportDialog
import hello.kwfriends.ui.component.finalCheckPopup
import hello.kwfriends.ui.screens.chattingList.ChattingListScreen
import hello.kwfriends.ui.screens.chattingList.ChattingsListViewModel
import hello.kwfriends.ui.screens.findGathering.FindGatheringScreen
import hello.kwfriends.ui.screens.home.HomeScreen
import hello.kwfriends.ui.screens.post.postInfo.PostInfoPopup
import hello.kwfriends.ui.screens.post.setPostData.SetPostDataPopup
import hello.kwfriends.ui.screens.post.setPostData.SetPostDataViewModel
import hello.kwfriends.ui.screens.settings.SettingsViewModel
import hello.kwfriends.ui.theme.KWFriendsTheme


@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    setPostDataViewModel: SetPostDataViewModel,
    settingsViewModel: SettingsViewModel,
    chattingsLIstViewModel: ChattingsListViewModel,
    mainNavigation: NavController
) {
    val postID = mainViewModel.postInfoPopupState.second

    val context = LocalContext.current
    val navController = rememberNavController()
    val homeNavigation = rememberMainNavigation(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var fabOpened by remember {
        mutableStateOf(true)
    }


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

    //포스트 정보 팝업
    PostInfoPopup(
        state = mainViewModel.postInfoPopupState.first,
        postDetail = mainViewModel.posts.find { it.postID == postID },
        onDismiss = { mainViewModel.postInfoPopupState = false to "" },
        onPostReport = { mainViewModel.postReportDialogState = true to postID },
        onPostDelete = {
            mainViewModel.finalCheckState = true
            mainViewModel.finalCheckPopupSet(
                title = "모임을 삭제할까요?",
                body = "삭제한 모임은 다시 되돌릴 수 없습니다.",
                onContinueAction = {
                    Log.d("postDelete", "postDelete")
                    mainViewModel.postDelete(postID)
                }
            )
        },
        mainViewModel = mainViewModel,
        enjoyButton = {
            EnjoyButton(
                postDetail = mainViewModel.posts.find { it.postID == postID },
                updateStatus = {
                    mainViewModel.updateParticipationStatus(
                        postID = postID,
                    )
                },
                editPostInfo = {
                    Log.d("actionState", "Action.MODIFY")
                    mainViewModel.setPostDataState =
                        Action.MODIFY to mainViewModel.postInfoPopupState.second
                }
            )
        }
    )

    // 모임 정보 설정 팝업
    SetPostDataPopup(
        state = mainViewModel.setPostDataState.first,
        onDismiss = {
            mainViewModel.setPostDataState = Action.NONE to ""
            Log.d("actionState", "Action.NONE")
        },
        setPostDataViewModel = setPostDataViewModel,
        postDetail = mainViewModel.posts.find { it.postID == mainViewModel.postInfoPopupState.second },
        mainViewModel = mainViewModel
    )

    //포스트 신고 다이얼로그
    PostReportDialog(state = mainViewModel.postReportDialogState.first,
        textList = mainViewModel.postReportTextList,
        onDismiss = { mainViewModel.postReportDialogState = false to "" },
        onPostReport = { mainViewModel.postReport(it) }
    )

    //유저 신고 다이얼로그
    UserReportDialog(
        state = mainViewModel.userReportDialogState.first,
        textList = mainViewModel.userReportTextList,
        onDismiss = { mainViewModel.userReportDialogState = false to "" },
        onUserReport = { mainViewModel.userReport(it) }
    )

    //유저 정보 팝업
    UserInfoPopup(
        state = mainViewModel.userInfoPopupState.first,
        uid = mainViewModel.userInfoPopupState.second,
        addUserIgnore = { mainViewModel.addUserIgnore(mainViewModel.userInfoPopupState.second) },
        removeUserIgnore = { mainViewModel.removeUserIgnore(mainViewModel.userInfoPopupState.second) },
        onDismiss = { mainViewModel.userInfoPopupState = false to "" },
        onUserReport = {
            mainViewModel.userReportDialogState =
                true to mainViewModel.userInfoPopupState.second
        },
        makeDirectChatting = {
            mainViewModel.makeDirectChatting(
                targetUid = mainViewModel.userInfoPopupState.second,
                mainNavigation = mainNavigation
            )
        }
    )

    // 사용자 조작 확인 팝업
    finalCheckPopup(
        state = mainViewModel.finalCheckState,
        title = mainViewModel.finalCheckTitle,
        body = mainViewModel.finalCheckBody,
        onContinue = {
            mainViewModel.finalCheckState = false
            mainViewModel.onContinueAction()
        },
        onDismiss = {
            mainViewModel.finalCheckState = false
        }
    )

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
                currentDestination = currentDestination?.route
            )
        },
        //플로팅 버튼
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabOpened,
                enter = slideInHorizontally(animationSpec = tween(durationMillis = 500))
                { fullWidth -> fullWidth } + fadeIn(animationSpec = tween(durationMillis = 500)),
                exit = slideOutHorizontally(animationSpec = tween(durationMillis = 500))
                { fullWidth -> fullWidth } + fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                ExtendedFloatingActionButton(
                    text = { Text(text = "모임 생성", style = MaterialTheme.typography.bodyMedium) },
                    icon = { Icon(Icons.Default.Add, "모임 생성") },
                    onClick = {
                        mainViewModel.setPostDataState = Action.ADD to ""
                    },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
            }
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
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            composable(MainDestination.FindGatheringScreen.route) {
                fabOpened = true
                FindGatheringScreen(
                    mainViewModel = mainViewModel,
                    setPostDataViewModel = setPostDataViewModel,
                    posts = mainViewModel.posts
                )
            }
            composable(MainDestination.HomeScreen.route) {
                fabOpened = true
                HomeScreen(
                    mainViewModel = mainViewModel,
                    homeNavigation = homeNavigation
                )
            }
            composable(MainDestination.ChatScreen.route) {
                fabOpened = false
                ChattingListScreen(
                    chattingsListViewModel = chattingsLIstViewModel,
                    navigation = mainNavigation
                )
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
            chattingsLIstViewModel = ChattingsListViewModel(),
            mainNavigation = navController
        )
    }
}
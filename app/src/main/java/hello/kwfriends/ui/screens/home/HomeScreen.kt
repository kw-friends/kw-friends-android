@file:OptIn(ExperimentalMaterialApi::class)

package hello.kwfriends.ui.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.component.EnjoyButton
import hello.kwfriends.ui.component.HomeTopAppBar
import hello.kwfriends.ui.component.NewPostPopup
import hello.kwfriends.ui.component.NoSearchResult
import hello.kwfriends.ui.component.PostInfoPopup
import hello.kwfriends.ui.component.ReportDialog
import hello.kwfriends.ui.component.TagChip
import hello.kwfriends.ui.screens.findGathering.FindGatheringItemList
import hello.kwfriends.ui.screens.newPost.NewPostViewModel
import hello.kwfriends.ui.screens.settings.SettingsViewModel
import hello.kwfriends.ui.theme.KWFriendsTheme


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    newPostViewModel: NewPostViewModel,
    settingsViewModel: SettingsViewModel,
    navigation: NavController
) {
    val context = LocalContext.current

    //유저 개인 설정 세팅값 받아오기
    if (!settingsViewModel.userSettingValuesLoaded) {
        settingsViewModel.userSettingValuesLoaded = true
        settingsViewModel.userSettingValuesLoad()
    }
    //아래로 당겨서 새로고침
    val pullRefreshState = rememberPullRefreshState(
        refreshing = homeViewModel.isRefreshing,
        onRefresh = {
            homeViewModel.refreshPost()
        }
    )
    //검색창에 대한 포커스
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(!homeViewModel.isSearching) {
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
        if (homeViewModel.isSearching) {
            homeViewModel.isSearching = false
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
    //태그 필터 리스트 스크롤 저장 변수
    val scrollState = rememberScrollState()

    Scaffold(
        //앱 바
        topBar = {
            HomeTopAppBar(
                navigation = navigation,
                isSearching = homeViewModel.isSearching,
                searchText = homeViewModel.searchText,
                setSearchText = { homeViewModel.setSearchContentText(it) },
                clickSearchButton = { homeViewModel.onclickSearchButton() },
                clickBackButton = { homeViewModel.isSearching = false },
                focusRequester = focusRequester,
            )
        },
        //플로팅 버튼
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "모임 생성") },
                icon = { Icon(Icons.Default.Add, null) },
                onClick = {
                    homeViewModel.newPostPopupState = true
                },
                modifier = Modifier.padding(bottom = 35.dp)
            )
        }
    ) { paddingValues ->
        //포스트 정보 다이얼로그
        PostInfoPopup(
            state = homeViewModel.postPopupState.first,
            postDetail = homeViewModel.posts.find { it.postID == homeViewModel.postPopupState.second },
            onDismiss = { homeViewModel.postPopupState = false to "" },
            onReport = {
                homeViewModel.reportDialogState =
                    true to homeViewModel.postPopupState.second
            },
            homeViewModel = homeViewModel,
            enjoyButton = {
                EnjoyButton(
                    postDetail = homeViewModel.posts.find { it.postID == homeViewModel.postPopupState.second },
                    updateStatus = {
                        homeViewModel.updateParticipationStatus(
                            postID = homeViewModel.postPopupState.second,
                            viewModel = homeViewModel
                        )
                    }
                )
            }
        )
        //모임 생성 다이얼로그
        NewPostPopup(
            state = homeViewModel.newPostPopupState,
            onDismiss = { homeViewModel.newPostPopupState = false },
            newPostViewModel = newPostViewModel
        )
        //신고 다이얼로그
        if (homeViewModel.reportDialogState.first) {
            homeViewModel.initReportChoice()
            ReportDialog(homeViewModel = homeViewModel)
        }
        //태그
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
//                .background(Color(0xFFE2A39B))
            ) {
                Spacer(Modifier.width(12.dp))
                homeViewModel.filterTagMap.forEach {
                    TagChip(
                        modifier = Modifier.padding(end = 4.dp),
                        text = it.key,
                        selected = it.value,
                        onClick = { homeViewModel.filterTagMap[it.key] = !it.value }
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            Box {
                Column(
                    modifier = Modifier
                        .pullRefresh(pullRefreshState)
                ) {
                    //검색중인지
                    if (homeViewModel.isSearching) {
                        if (homeViewModel.searchText != "" && homeViewModel.searchingPosts.isEmpty()) {
                            //검색 결과 없을때 표시할 화면
                            NoSearchResult(homeViewModel.searchText)
                        } else {
                            //검색 결과 화면
                            FindGatheringItemList(
                                homeViewModel.filter(homeViewModel.searchingPosts),
                                viewModel = homeViewModel
                            )
                        }
                    }
                    //검색중 아닐때는 모든 모임 목록 표시
                    else {
                        FindGatheringItemList(
                            homeViewModel.filter(homeViewModel.posts),
                            viewModel = homeViewModel
                        )
                    }
                }
                //로딩 아이콘
                PullRefreshIndicator(
                    refreshing = homeViewModel.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
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
        HomeScreen(
            homeViewModel = HomeViewModel(),
            newPostViewModel = NewPostViewModel(),
            settingsViewModel = SettingsViewModel(),
            navigation = navController
        )
    }
}
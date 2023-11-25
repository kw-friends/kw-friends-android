@file:OptIn(ExperimentalMaterialApi::class)

package hello.kwfriends.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hello.kwfriends.ui.component.HomeTopAppBar
import hello.kwfriends.ui.main.Routes
import hello.kwfriends.ui.screens.findGathering.FindGatheringCardList
import hello.kwfriends.ui.screens.settings.SettingsViewModel


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    navigation: NavController
) {
    //유저 개인 설정 세팅값 받아오기
    if (!settingsViewModel.userSettingValuesLoaded) {
        settingsViewModel.userSettingValuesLoaded = true
        settingsViewModel.userSettingValuesLoad()
    }
    //post 목록 불러오기
    LaunchedEffect(true) {
        homeViewModel.getPostFromFirestore()
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
    Scaffold(
        //앱 바
        topBar = {
            HomeTopAppBar(
                navigation =  navigation,
                isSearching = homeViewModel.isSearching,
                searchText = homeViewModel.searchContent,
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
                    navigation.navigate(Routes.NEW_POST_SCREEN)
                },
                modifier = Modifier.padding(bottom = 35.dp)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .pullRefresh(pullRefreshState)
        ) {
            //검색중인지
            if(homeViewModel.isSearching) {
                if(homeViewModel.searchContent != "" && homeViewModel.searchingPosts.isEmpty()) {
                    //검색 결과 없을때 표시할 화면
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(modifier = Modifier.weight(1f))
                        Column(
                            modifier = Modifier.weight(6f),
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = " \"${homeViewModel.searchContent}\"에 대한 검색 결과가 없습니다",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 20.dp),
                                maxLines = 100
                            )
                        }
                    }
                }
                else {
                    //검색 결과 화면
                    FindGatheringCardList(homeViewModel.searchingPosts, viewModel = homeViewModel)
                }
            }
            //검색중 아닐때는 모든 모임 목록 표시
            else {
                FindGatheringCardList(homeViewModel.posts, viewModel = homeViewModel)
            }
            //로딩 아이콘
            PullRefreshIndicator(
                refreshing = homeViewModel.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}
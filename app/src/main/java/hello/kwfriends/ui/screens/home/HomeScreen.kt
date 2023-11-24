@file:OptIn(ExperimentalMaterialApi::class)

package hello.kwfriends.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hello.kwfriends.ui.component.HomeTopAppBar
import hello.kwfriends.ui.main.Routes
import hello.kwfriends.ui.screens.findGathering.FindGatheringCardList
import hello.kwfriends.ui.screens.settings.SettingsViewModel


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(!homeViewModel.isSearching) {
        focusRequester.requestFocus()
    }
    Scaffold(
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
            if(homeViewModel.isSearching) {
                FindGatheringCardList(homeViewModel.searchingPosts, viewModel = homeViewModel)
            }
            else {
                FindGatheringCardList(homeViewModel.posts, viewModel = homeViewModel)
            }
            PullRefreshIndicator(
                refreshing = homeViewModel.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}
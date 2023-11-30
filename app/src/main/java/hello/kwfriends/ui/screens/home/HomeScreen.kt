@file:OptIn(ExperimentalMaterialApi::class)

package hello.kwfriends.ui.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hello.kwfriends.ui.component.CheckboxStyle2
import hello.kwfriends.ui.component.HomeTopAppBar
import hello.kwfriends.ui.component.NoSearchResult
import hello.kwfriends.ui.component.TagChip
import hello.kwfriends.ui.main.Routes
import hello.kwfriends.ui.screens.findGathering.FindGatheringCardList
import hello.kwfriends.ui.screens.settings.SettingsViewModel


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
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
    //뒤로 가기 버튼을 눌렀을 때 실행할 코드
    BackHandler {
        if(homeViewModel.isSearching) {
            homeViewModel.isSearching = false
        }
    }
    //태그 필터 리스트 스크롤 저장 변수
    val scrollState = rememberScrollState()

    Scaffold(
        //앱 바
        topBar = {
            HomeTopAppBar(
                navigation =  navigation,
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
                    navigation.navigate(Routes.NEW_POST_SCREEN)
                },
                modifier = Modifier.padding(bottom = 35.dp)
            )
        }
    ) { paddingValues ->
        if(homeViewModel.reportDialogState.first) {
            homeViewModel.initReportChoice()
            AlertDialog(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                title = { Text(text = "신고 사유 선택", fontSize = 20.sp) },
                text = {
                    Column {
                        for(reportText in homeViewModel.reportTextList) {
                            CheckboxStyle2(
                                modifier = Modifier.padding(vertical = 8.dp),
                                text = reportText,
                                textColor = Color.Black,
                                fontSize = 17.sp,
                                checkBoxSize = 17.dp,
                                checked = reportText in homeViewModel.reportChoice,
                                onClicked = {
                                    if(reportText in homeViewModel.reportChoice) {
                                        homeViewModel.reportChoice = ArrayList(homeViewModel.reportChoice).apply { remove(reportText) }
                                    }
                                    else {
                                        homeViewModel.reportChoice = ArrayList(homeViewModel.reportChoice).apply { add(reportText) }
                                    }
                                    Log.w("Lim", "reportChoice: ${homeViewModel.reportChoice}")
                                }
                            )
                        }
                    }
                },
                onDismissRequest = { homeViewModel.reportDialogState = false to null },
                confirmButton = {
                    TextButton(onClick = { homeViewModel.report() }) {
                        Text(text = "신고")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { homeViewModel.reportDialogState = false to null }) {
                        Text(text = "취소")
                    }
                }
            )
        }
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .background(Color(0xFFE2A39B))
            ) {
                Spacer(Modifier.width(8.dp))
                homeViewModel.filterTagMap.forEach {
                    TagChip(
                        modifier = Modifier.padding(4.dp),
                        text = it.key,
                        icon = Icons.Filled.Person,
                        selected = it.value,
                        onClick = { homeViewModel.filterTagMap[it.key] = !it.value }
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            Box {
                Column(
                    modifier = Modifier.pullRefresh(pullRefreshState)
                ) {

                    //검색중인지
                    if(homeViewModel.isSearching) {
                        if(homeViewModel.searchText != "" && homeViewModel.searchingPosts.isEmpty()) {
                            //검색 결과 없을때 표시할 화면
                            NoSearchResult(homeViewModel.searchText)
                        }
                        else {
                            //검색 결과 화면
                            FindGatheringCardList(
                                homeViewModel.filter(homeViewModel.searchingPosts),
                                viewModel = homeViewModel
                            )
                        }
                    }
                    //검색중 아닐때는 모든 모임 목록 표시
                    else {
                        FindGatheringCardList(
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
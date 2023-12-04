@file:OptIn(ExperimentalMaterialApi::class)

package hello.kwfriends.ui.screens.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.ui.component.EnjoyButton
import hello.kwfriends.ui.component.HomeTopAppBar
import hello.kwfriends.ui.component.NoSearchResult
import hello.kwfriends.ui.component.ReportDialog
import hello.kwfriends.ui.component.TagChip
import hello.kwfriends.ui.main.Routes
import hello.kwfriends.ui.screens.findGathering.FindGatheringItemList
import hello.kwfriends.ui.screens.settings.SettingsViewModel
import hello.kwfriends.ui.theme.KWFriendsTheme


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
        //포스트 정보 다이얼로그
        if (homeViewModel.postDialogState.first && homeViewModel.postDialogState.second != null) {
            var menuExpanded by remember { mutableStateOf(false) }
            Dialog(
                onDismissRequest = { homeViewModel.postDialogState = false to null },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                ) {
                    IconButton(
                        modifier = Modifier.align(Alignment.TopStart),
                        onClick = { homeViewModel.postDialogState = false to null }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "back button"
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(
                            modifier = Modifier.align(Alignment.TopEnd),
                            onClick = { menuExpanded = true }
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "post menu")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("신고") },
                                onClick = {
                                    menuExpanded = false
                                    homeViewModel.reportDialogState =
                                        true to homeViewModel.postDialogState.second?.postID
                                },
//                        leadingIcon = {
//                            Icon(
//                                Icons.Outlined.Details,
//                                contentDescription = null
//                            )
//                        },
                                //trailingIcon = { Text("F11", textAlign = TextAlign.Center) }
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 60.dp, horizontal = 15.dp)
                    ) {
                        //탑
                        Text(text = homeViewModel.postDialogState.second?.gatheringTitle ?: "", style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Default, fontWeight = FontWeight(600))
                        Text(
                            text = homeViewModel.postDialogState.second?.gatheringDescription ?: "", style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Default
                        )
                        //Text(text = homeViewModel.postDialogState.second?.gatheringLocation ?: "")
                        //Text(text = homeViewModel.postDialogState.second?.gatheringTime ?: "")
                        Row(modifier = Modifier.padding(top = 10.dp)) {
                            homeViewModel.postDialogState.second?.gatheringTags?.forEach {
                                Text(
                                    text = "#${it}",
                                    modifier = Modifier.padding(end = 4.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        //바텀
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                            Divider(
                                modifier = Modifier.padding(vertical = 20.dp),
                                color = Color.Gray,
                                thickness = 0.5.dp,
                            )
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = "참여 인원  ${homeViewModel.currentParticipationStatusMap[homeViewModel.postDialogState.second?.postID]}/${homeViewModel.postDialogState.second?.maximumParticipants}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight(400)
                                )
                                Spacer(Modifier.height(15.dp))
                                Row {
                                    //참여자 목록
                                    repeat(homeViewModel.currentParticipationStatusMap[homeViewModel.postDialogState.second?.postID] ?:0) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            AsyncImage(
                                                model = R.drawable.test_image,
                                                placeholder = painterResource(id = R.drawable.profile_default_image),
                                                contentDescription = "My profile image",
                                                modifier = Modifier
                                                    .padding(end = 15.dp)
                                                    .size(50.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop,
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                text = "참여자${it+1}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontFamily = FontFamily.Default,
                                            )
                                        }

                                    }
                                }
                            }
                            Spacer(Modifier.height(30.dp))
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                EnjoyButton(
                                    status = homeViewModel.participationStatusMap[homeViewModel.postDialogState.second?.postID],
                                    updateStatus = {
                                        homeViewModel.updateParticipationStatus(
                                            postID = homeViewModel.postDialogState.second?.postID ?: "",
                                            viewModel = homeViewModel
                                        )
                                    }
                                )
                            }

                        }
                    }
                }
            }
        }
        //신고 다이얼로그
        if(homeViewModel.reportDialogState.first) {
            homeViewModel.initReportChoice()
            ReportDialog(homeViewModel = homeViewModel)
        }
        //태그
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
fun HomeScreenPreview(){
    val navController = rememberNavController()
    KWFriendsTheme {
        HomeScreen(homeViewModel = HomeViewModel(), settingsViewModel = SettingsViewModel(), navigation = navController)
    }
}
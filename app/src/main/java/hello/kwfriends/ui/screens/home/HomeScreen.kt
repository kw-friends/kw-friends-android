@file:OptIn(ExperimentalMaterialApi::class)

package hello.kwfriends.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hello.kwfriends.ui.component.SearchTextField
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = !homeViewModel.isSearching,
                        enter = fadeIn(),
                        exit = fadeOut(animationSpec = tween(durationMillis = 100))
                    ) {
                        Text(
                            text = "내 모임",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(5.dp),
                            maxLines = 1
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE2A39B)
                ),
                actions = {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                        ) {
                            if (homeViewModel.isSearching) {
                                IconButton(
                                    onClick = {
                                        homeViewModel.isSearching = !homeViewModel.isSearching
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBackIosNew,
                                        contentDescription = "Exit",
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                            SearchTextField(
                                value = "",
                                onValueChange = {},
                                modifier = Modifier
                                    .animateContentSize(animationSpec = tween(easing = LinearOutSlowInEasing))
                                    .width(if (homeViewModel.isSearching) 290.dp else 0.dp)
                            )
                        }
                        IconButton(
                            onClick = { homeViewModel.isSearching = !homeViewModel.isSearching },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .align(Alignment.CenterEnd)

                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(35.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { navigation.navigate(Routes.SETTINGS_SCREEN) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                },
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
            FindGatheringCardList(viewModel = homeViewModel)
            PullRefreshIndicator(
                refreshing = homeViewModel.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}
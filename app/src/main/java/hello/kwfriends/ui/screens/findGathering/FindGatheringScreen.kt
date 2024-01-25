package hello.kwfriends.ui.screens.findGathering

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.ui.component.NoSearchResult
import hello.kwfriends.ui.component.TagChip
import hello.kwfriends.ui.screens.main.MainViewModel
import hello.kwfriends.ui.screens.post.setPostData.SetPostDataViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FindGatheringScreen(
    posts: List<PostDetail>,
    mainViewModel: MainViewModel,
    setPostDataViewModel: SetPostDataViewModel
) {
    //태그 필터 리스트 스크롤 저장 변수
    val tagsHorizontalScrollState = rememberScrollState()

    //아래로 당겨서 새로고침
    val pullRefreshState = rememberPullRefreshState(
        refreshing = mainViewModel.isRefreshing,
        onRefresh = {
            mainViewModel.refreshPost()
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        //태그
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(tagsHorizontalScrollState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(12.dp))
            TagChip(
                modifier = Modifier.padding(end = 4.dp),
                text = "참여 중",
                selected = mainViewModel.onlyParticipatedGathering,
                onClick = {
                    mainViewModel.onlyParticipatedGathering =
                        !mainViewModel.onlyParticipatedGathering
                }
            )
            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .width(1.4f.dp)
                    .height(30.dp)
            )
            Spacer(Modifier.width(4.dp))
            mainViewModel.filterTagMap.forEach {
                TagChip(
                    modifier = Modifier.padding(end = 4.dp),
                    text = it.key,
                    selected = it.value,
                    onClick = { mainViewModel.filterTagMap[it.key] = !it.value }
                )
            }
            Spacer(Modifier.width(8.dp))
        }
        Box {
            Column(
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .padding(horizontal = 10.dp)
            ) {
                //검색중인지
                if (mainViewModel.isSearching) {
                    if (mainViewModel.searchText != "" && mainViewModel.searchingPosts.isEmpty()) {
                        //검색 결과 없을때 표시할 화면
                        NoSearchResult(mainViewModel.searchText)
                    } else {
                        //검색 결과 화면
                        GatheringList(
                            mainViewModel.filter(mainViewModel.searchingPosts),
                            mainViewModel = mainViewModel,
                            maximumItems = null,
                            showParticipationStatus = true,
                            showNoSearchResultMessage = true
                        )
                    }
                } else { // 검색중 아닐때는 모든 모임 목록 표시
                    GatheringList(
                        mainViewModel.filter(mainViewModel.posts),
                        mainViewModel = mainViewModel,
                        maximumItems = null,
                        logo = true,
                        showParticipationStatus = true,
                        showNoSearchResultMessage = true
                    )
                }
            }
            //로딩 아이콘
            PullRefreshIndicator(
                refreshing = mainViewModel.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
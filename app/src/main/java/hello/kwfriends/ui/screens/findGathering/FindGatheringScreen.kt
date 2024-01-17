package hello.kwfriends.ui.screens.findGathering

import android.util.Log
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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.realtimeDatabase.Action
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.ui.screens.post.postInfo.PostInfoPopup
import hello.kwfriends.ui.screens.post.setPostData.SetPostDataPopup
import hello.kwfriends.ui.screens.post.setPostData.SetPostDataViewModel
import hello.kwfriends.ui.component.EnjoyButton
import hello.kwfriends.ui.component.NoSearchResult
import hello.kwfriends.ui.component.PostReportDialog
import hello.kwfriends.ui.component.TagChip
import hello.kwfriends.ui.component.UserInfoPopup
import hello.kwfriends.ui.component.UserReportDialog
import hello.kwfriends.ui.component.finalCheckPopup
import hello.kwfriends.ui.screens.main.MainViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FindGatheringScreen(
    posts: List<PostDetail>,
    mainViewModel: MainViewModel,
    setPostDataViewModel: SetPostDataViewModel
) {
    val postID = mainViewModel.postInfoPopupState.second

    //태그 필터 리스트 스크롤 저장 변수
    val tagsHorizontalScrollState = rememberScrollState()

    //아래로 당겨서 새로고침
    val pullRefreshState = rememberPullRefreshState(
        refreshing = mainViewModel.isRefreshing,
        onRefresh = {
            mainViewModel.refreshPost()
        }
    )

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

    //태그
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(tagsHorizontalScrollState)
//                .background(Color(0xFFE2A39B))
    ) {
        Spacer(Modifier.width(12.dp))
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
        ) {
            //검색중인지
            if (mainViewModel.isSearching) {
                if (mainViewModel.searchText != "" && mainViewModel.searchingPosts.isEmpty()) {
                    //검색 결과 없을때 표시할 화면
                    NoSearchResult(mainViewModel.searchText)
                } else {
                    //검색 결과 화면
                    FindGatheringList(
                        mainViewModel.filter(mainViewModel.searchingPosts),
                        mainViewModel = mainViewModel
                    )
                }
            } else { // 검색중 아닐때는 모든 모임 목록 표시
                FindGatheringList(
                    mainViewModel.filter(mainViewModel.posts),
                    mainViewModel = mainViewModel
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
    Column(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        FindGatheringList(posts = posts, mainViewModel = mainViewModel)
    }
}
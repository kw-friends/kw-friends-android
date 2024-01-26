package hello.kwfriends.ui.screens.findGathering

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.firebase.realtimeDatabase.ServerData
import hello.kwfriends.preferenceDatastore.UserDataStore
import hello.kwfriends.ui.screens.main.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private val uid = Firebase.auth.currentUser!!.uid

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun GatheringItem(
    postDetail: PostDetail,
    viewModel: MainViewModel,
    participationStatus: ParticipationStatus?,
    onReport: () -> Unit,
    myParticipationStatus: ParticipationStatus
) {
    var reportMenuExpended by remember { mutableStateOf(false) }

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = Color(0xFFFAF3F3),
        ),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = { viewModel.postInfoPopupState = true to postDetail.postID },
                onLongClick = { reportMenuExpended = true }
            ),
        headlineContent = {
            DropdownMenu(
                expanded = reportMenuExpended,
                onDismissRequest = { reportMenuExpended = false }
            ) {
                if (myParticipationStatus == ParticipationStatus.NOT_PARTICIPATED) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "신고",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        enabled = !postDetail.reporters.containsKey(uid) && postDetail.gatheringPromoterUID != uid,
                        onClick = { onReport() },
                        trailingIcon = {
                            if (postDetail.reporters.containsKey(uid)) {
                                Icon(
                                    Icons.Default.Check,
                                    tint = Color.Gray,
                                    contentDescription = "check icon"
                                )
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "모임 참여",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        enabled = !viewModel.isProcessing,
                        onClick = {
                            viewModel.updateParticipationStatus(postID = postDetail.postID)
                        }
                    )
                }
                if (participationStatus == ParticipationStatus.PARTICIPATED){
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "모임 나가기",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        enabled = !viewModel.isProcessing,
                        onClick = {
                            viewModel.updateParticipationStatus(postID = postDetail.postID)
                        }
                    )
                }
            }

            Column {
                Text(
                    postDetail.gatheringTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight(500),
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    postDetail.gatheringDescription.replace("\n", " "),
                    maxLines = 2,
                    style = MaterialTheme.typography.bodySmall.merge(
                        TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    ),
                    color = Color.DarkGray,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlowRow(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = SimpleDateFormat(
                                "yyyy/MM/dd HH:mm",
                                Locale.getDefault()
                            ).format(
                                postDetail.timestamp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                        )
                        if (postDetail.gatheringTags.isNotEmpty()) {
                            Divider(
                                color = Color.LightGray,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(16.dp)
                                    .width(1.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        postDetail.gatheringTags.forEach {
                            Text(
                                text = "#$it ",
                                style = MaterialTheme.typography.labelSmall.merge(
                                    TextStyle(
                                        platformStyle = PlatformTextStyle(
                                            includeFontPadding = false
                                        )
                                    )
                                ),
                                color = Color.Gray,
                            )
                        }
                    }

                }
            }
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${postDetail.participants.count()}/${postDetail.maximumParticipants}",
                    style = MaterialTheme.typography.labelSmall
                )
                if (participationStatus == ParticipationStatus.PARTICIPATED) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(vertical = 4.dp)
                    )
                }
                if (participationStatus == ParticipationStatus.MY_GATHERING) {
                    Icon(
                        imageVector = Icons.Default.EmojiPeople,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(vertical = 4.dp)
                    )
                }
            }
        },
    )
}


@Composable
fun GatheringList(
    posts: List<PostDetail>,
    mainViewModel: MainViewModel,
    maximumItems: Int?,
    logo: Boolean = true,
    showParticipationStatus: Boolean,
    showNoSearchResultMessage: Boolean,
) {
    LazyColumn(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))

    ) {
        // TODO 일정 모임 개수만 가져오기 구현.
        // 근데 어차피 한번에 모든 모임을 가져오는데 필요한가?? 나중에 이미지 가져오는것도 생기면 필요할수도 ㅇㅇ
        items(posts.take(maximumItems ?: 999)) { postData ->
            if (
                postData.reporters.size < ServerData.data?.get("hideReportCount").toString()
                    .toInt()
                && postData.gatheringPromoterUID !in UserDataStore.userIgnoreList
            ) { //신고 n개 이상이면 숨기기
                GatheringItem(
                    postDetail = postData,
                    viewModel = mainViewModel,
                    participationStatus = if (showParticipationStatus) postData.myParticipantStatus else null,
                    onReport = {
                        mainViewModel.postReportDialogState = true to postData.postID
                    },
                    myParticipationStatus = postData.myParticipantStatus
                )
            }
        }

        if (posts.isEmpty() && showNoSearchResultMessage) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "검색 결과가 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        if (logo && posts.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "KW Friends",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun GatheringItemListPreview() {
    GatheringList(
        posts = listOf(
            PostDetail(
                gatheringTitle = "Preview",
                maximumParticipants = "Preview",
                gatheringTime = 0L,
                gatheringDescription = "Preview",
                gatheringTags = listOf("Preview"),
                gatheringLocation = "",
                gatheringPromoter = "",
                participants = emptyMap(),
                myParticipantStatus = ParticipationStatus.PARTICIPATED,
                postID = "Preview",
            )
        ),
        mainViewModel = MainViewModel(),
        maximumItems = null,
        showParticipationStatus = true,
        showNoSearchResultMessage = true
    )
}
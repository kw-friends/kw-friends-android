package hello.kwfriends.ui.screens.findGathering

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.firebase.realtimeDatabase.ServerData
import hello.kwfriends.preferenceDatastore.UserDataStore
import hello.kwfriends.ui.screens.main.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GatheringListItem(
    postDetail: PostDetail,
    viewModel: MainViewModel
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = Color(0xFFFAF3F3),
        ),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                viewModel.postInfoPopupState = true to postDetail.postID
            },
        headlineContent = {
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
        trailingContent = { Text(text = "${postDetail.participants.count()}/${postDetail.maximumParticipants}") },
    )
}


@Composable
fun FindGatheringList(posts: List<PostDetail>, mainViewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
    ) {
        items(posts) { postData ->
            if (
                postData.reporters.size < ServerData.data?.get("hideReportCount").toString()
                    .toInt()
                && postData.gatheringPromoterUID !in UserDataStore.userIgnoreList
            ) { //신고 n개 이상이면 숨기기
                GatheringListItem(
                    postDetail = postData,
                    viewModel = mainViewModel
                )
            }
        }

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

@Preview
@Composable
fun GatheringItemListPreview() {
    FindGatheringList(
        listOf(
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
        mainViewModel = MainViewModel()
    )
}
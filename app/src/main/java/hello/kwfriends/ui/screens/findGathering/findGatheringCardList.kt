package hello.kwfriends.ui.screens.findGathering

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.ui.screens.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GathergingListItem(
    postDetail: PostDetail,
    viewModel: HomeViewModel
) {
    Column {
        ListItem(
            modifier = Modifier.clickable {
                viewModel.postPopupState = true to postDetail.postID
            },
            headlineContent = {
                Column(Modifier.padding(vertical = 7.dp)) {
                    Text(
                        postDetail.gatheringTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight(500)
                    )
                    Text(
                        postDetail.gatheringDescription.replace("\n\n", "\n"),
                        maxLines = 2,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Default,
                        color = Color.DarkGray
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FlowRow(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(
                                    postDetail.timestamp
                                ),
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Default,
                                color = Color.Gray
                            )
                            if (postDetail.gatheringTags.isNotEmpty()) {
                                Divider(
                                    color = Color.LightGray,
                                    modifier = Modifier
                                        .padding(start = 6.dp, end = 6.dp, top = 3.dp)
                                        .height(10.dp)
                                        .width(1.dp)
                                )
                            }
                            postDetail.gatheringTags.forEach {
                                Text(
                                    text = "#$it ",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Default,
                                    color = Color.Gray
                                )
                            }
                        }

                    }
                }
            },
            trailingContent = { Text("${viewModel.participantsCountMap[postDetail.postID]}/${postDetail.maximumParticipants}") },
        )
    }
}


@Composable
fun FindGatheringItemList(posts: List<PostDetail>, viewModel: HomeViewModel) {
    LazyColumn {
        items(posts) { postData ->
            if(postData.reporters.size < 5) { //신고 5개 이상이면 숨기기
                GathergingListItem(
                    postDetail = postData,
                    viewModel = viewModel
                )
                Divider(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    color = Color.LightGray,
                    thickness = 0.5.dp,
                )
            }
        }
    }
}

@Preview
@Composable
fun GathergingItemListPreview() {
    FindGatheringItemList(
        listOf(
            PostDetail(
                gatheringTitle = "Preview",
                maximumParticipants = "Preview",
                gatheringTime = "Preview",
                gatheringDescription = "Preview",
                gatheringTags = listOf("Preview"),
                gatheringLocation = "",
                gatheringPromoter = "",
                participants = emptyMap(),
                myParticipantStatus = ParticipationStatus.PARTICIPATED,
                postID = "Preview",
            )
        ),
        viewModel = HomeViewModel()
    )
}
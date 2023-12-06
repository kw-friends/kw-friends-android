package hello.kwfriends.ui.screens.findGathering

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.firestoreDatabase.ParticipationStatus
import hello.kwfriends.firebase.firestoreDatabase.PostDetail
import hello.kwfriends.ui.screens.home.HomeViewModel

@Composable
fun GathergingListItem(
    postDetail: PostDetail,
    viewModel: HomeViewModel
) {
    val currentParticipationStatus = viewModel.currentParticipationStatusMap[postDetail.postID]
    Column {
        ListItem(
            modifier = Modifier.clickable {
                viewModel.postPopupState = true to postDetail
            },
            headlineContent = {
                Column(Modifier.padding(vertical = 7.dp)) {
                    Text(postDetail.gatheringTitle, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Default, fontWeight = FontWeight(500))
                    Text(postDetail.gatheringDescription.replace("\n\n", "\n"), maxLines = 2, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Default, color = Color.DarkGray)
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(text = "n분전", maxLines = 1, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Default, color = Color.Gray)
                        if(postDetail.gatheringTags.isNotEmpty()) Text(" | ",  style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Default, color = Color.DarkGray)
                        postDetail.gatheringTags.forEach {
                            Text(text = "#$it ", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Default, color = Color.Gray)
                        }
                    }
                }
            },
            trailingContent = { Text("$currentParticipationStatus/${postDetail.maximumParticipants}") },
        )
        Divider(
            modifier = Modifier.padding(horizontal = 5.dp),
            color = Color.Gray,
            thickness = 0.5.dp,
        )
    }
}


@Composable
fun FindGatheringItemList(posts: List<PostDetail>, viewModel: HomeViewModel) {
    LazyColumn {
        items(posts) { postData ->
            GathergingListItem(
                postDetail = postData,
                viewModel = viewModel
            )
        }
    }
}

@Preview
@Composable
fun GathergingItemListPreview() {
    FindGatheringItemList(listOf(PostDetail(
        gatheringTitle = "Preview",
        minimumParticipants = "Preview",
        maximumParticipants = "Preview",
        gatheringTime = "Preview",
        gatheringDescription = "Preview",
        gatheringTags = listOf("Preview"),
        gatheringLocation = "",
        gatheringPromoter = "",
        currentParticipants = "",
        participantStatus = ParticipationStatus.PARTICIPATED,
        postID = "Preview",
    )),
        viewModel = HomeViewModel()
    )
}
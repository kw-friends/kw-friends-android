package hello.kwfriends.ui.screens.findGathering

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.firestoreDatabase.ParticipationStatus
import hello.kwfriends.firebase.firestoreDatabase.PostDetail
import hello.kwfriends.ui.screens.home.HomeViewModel

@Composable
fun GathergingListItem(
    title: String,
    maximumParticipants: String,
    description: String,
    tags: List<String>,
    postID: String,
    viewModel: HomeViewModel
) {
    //val participationStatus = viewModel.participationStatusMap[postID]
    val currentParticipationStatus = viewModel.currentParticipationStatusMap[postID]
    Column {
        ListItem(
            headlineContent = {
                Column(Modifier.padding(vertical = 7.dp)) {
                    Text(title, style = MaterialTheme.typography.bodyLarge, fontFamily = FontFamily.Default)
                    Text(description, maxLines = 2, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Default, color = Color.DarkGray)
                    Row {
                        Text(text = "n분전", maxLines = 1, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Default, color = Color.Gray)
                        if(tags.isNotEmpty()) Text(" | ",  style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Default, color = Color.DarkGray)
                        tags.forEach { 
                            Text(text = "#$it ", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Default, color = Color.Gray)
                        }
                    }
                }
            },
            trailingContent = { Text("$currentParticipationStatus/$maximumParticipants") },
        )
        Divider(
            modifier = Modifier.padding(horizontal = 5.dp),
            color = Color.Gray,
            thickness = 0.5.dp,
        )
    }
}
@Preview
@Composable
fun GathergingListItemPreview() {
    FindGatheringCardList(listOf(PostDetail(
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


@Composable
fun FindGatheringCardList(posts: List<PostDetail>, viewModel: HomeViewModel) {
    LazyColumn {
        items(posts) { postData ->
            GathergingListItem(
                title = postData.gatheringTitle,
                maximumParticipants = postData.maximumParticipants,
                description = postData.gatheringDescription,
                tags = postData.gatheringTags,
                postID = postData.postID,
                viewModel = viewModel
            )
        }
    }
}

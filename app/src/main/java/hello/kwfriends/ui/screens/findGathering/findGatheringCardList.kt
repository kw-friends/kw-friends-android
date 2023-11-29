package hello.kwfriends.ui.screens.findGathering

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Details
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.firestoreDatabase.PostDetail
import hello.kwfriends.ui.component.EnjoyButton
import hello.kwfriends.ui.screens.home.HomeViewModel

@Composable
fun GatheringCard(
    title: String,
    location: String,
    promoter: String,
    minimumParticipants: String,
    maximumParticipants: String,
    time: String, //추후 datetime으로 변경,
    description: String,
    tags: List<String>,
    postID: String,
    viewModel: HomeViewModel
) {
    var descriptionOpened by remember {
        mutableStateOf(false)
    }

    val participationStatus = viewModel.participationStatusMap[postID]
    val currentParticipationStatus = viewModel.currentParticipationStatusMap[postID]

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable(onClick = { descriptionOpened = !descriptionOpened })
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "post menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("신고") },
                        onClick = {
                            viewModel.reportDialogState = true to postID
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Details,
                                contentDescription = null
                            )
                        },
                        //trailingIcon = { Text("F11", textAlign = TextAlign.Center) }
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight(600)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(width = 4.dp))
                    Text(text = location, style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(width = 4.dp))
                    Text(text = time, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = "최대 ${maximumParticipants}명 중 ${currentParticipationStatus}명 참여\n" +
                            "최소 인원: $minimumParticipants\n" +
                            "주최자: $promoter",
                    modifier = Modifier
                        .padding(end = 2.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Row {
                    tags.forEach {
                        Text(
                            text = "#${it}",
                            modifier = Modifier.padding(end = 4.dp),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                AnimatedVisibility(descriptionOpened) {
                    Divider(
                        color = Color(0xFF353535),
                        thickness = 0.5.dp
                    )
                    Column(
                        modifier = Modifier.padding(bottom = 12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (description != "") {
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        EnjoyButton(status = participationStatus,
                            updateStatus = {
                                viewModel.updateParticipationStatus(
                                    postID = postID,
                                    viewModel = viewModel
                                )
                            })
                    }
                }
            }
        }
    }
}


@Composable
fun FindGatheringCardList(posts: List<PostDetail>, viewModel: HomeViewModel) {
    LazyColumn {
        items(posts) { postData ->
            GatheringCard(
                title = postData.gatheringTitle,
                location = postData.gatheringLocation,
                minimumParticipants = postData.minimumParticipants,
                maximumParticipants = postData.maximumParticipants,
                time = postData.gatheringTime,
                promoter = postData.gatheringPromoter,
                description = postData.gatheringDescription,
                tags = postData.gatheringTags,
                postID = postData.postID,
                viewModel = viewModel

            )
        }
    }
}

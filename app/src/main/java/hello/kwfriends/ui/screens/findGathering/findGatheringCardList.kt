package hello.kwfriends.ui.screens.findGathering

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import hello.kwfriends.firebase.firestoreManager.ParticipationStatus
import hello.kwfriends.ui.screens.main.MainViewModel

@Composable
fun GatheringCard(
    title: String,
    location: String,
    promoter: String,
    minimumParticipants: String,
    maximumParticipants: String,
    time: String, //추후 datetime으로 변경,
    description: String,
    postID: String,
    viewModel: MainViewModel
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
        Box(modifier = Modifier.fillMaxSize()) {
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
                        Button(
                            onClick = {
                                if (participationStatus == ParticipationStatus.PARTICIPATED
                                    || participationStatus == ParticipationStatus.NOT_PARTICIPATED
                                ) {
                                    viewModel.updateParticipationStatus(
                                        postID = postID,
                                        viewModel = viewModel
                                    )
                                }
                            },
                            enabled =
                            participationStatus == ParticipationStatus.PARTICIPATED
                                    || participationStatus == ParticipationStatus.NOT_PARTICIPATED
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .animateContentSize()
                            ) {
                                when (participationStatus) {
                                    ParticipationStatus.PARTICIPATED -> {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "participated"
                                            )
                                            Spacer(modifier = Modifier.size(7.dp))
                                            Text(
                                                text = "참가 완료",
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        }
                                    }

                                    ParticipationStatus.GETTING_IN -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.size(7.dp))
                                        Text(
                                            text = "참가 중..",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }

                                    ParticipationStatus.GETTING_OUT -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.size(7.dp))
                                        Text(
                                            text = "나가는 중..",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }

                                    else -> { // participationsStatus == ParticipationStatus.NOT_PARTICIPATED
                                        Text(
                                            text = "모임 참여",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FindGatheringCardList(viewModel: MainViewModel) {
    val posts = viewModel.posts
    viewModel.getPostFromFirestore(viewModel = viewModel)
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
                postID = postData.postID,
                viewModel = viewModel

            )
        }
    }
}

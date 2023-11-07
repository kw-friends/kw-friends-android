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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import hello.kwfriends.ui.screens.main.MainViewModel

@Composable
fun GatheringCard(
    title: String,
    location: String,
    currentParticipants: String,
    minimumParticipants: String,
    maximumParticipants: String,
    time: String, //추후 datetime으로 변경,
    description: String
) {
    var descriptionOpened by remember {
        mutableStateOf(false)
    }

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
                    text = "최대 ${maximumParticipants}명 중 ${currentParticipants}명 참여\n최소 인원: $minimumParticipants",
                    modifier = Modifier
                        .padding(end = 2.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                AnimatedVisibility(descriptionOpened) {
                    Divider(
                        color = Color(0xFF353535),
                        thickness = 0.5.dp
                    )
                    Column(modifier = Modifier.padding(end = 4.dp)) {
                        if (description != "") {
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        Button(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "모임 참여", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

        }
    }
    /*Column(
        modifier = Modifier
            .padding(16.dp)
            .border(
                border = BorderStroke(width = 1.dp, color = Color(65, 65, 65, 255)),
                shape = AbsoluteRoundedCornerShape(corner = CornerSize(30.dp)),
            )
            .background(color = Color(0xffffffff))
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 22.dp, top = 18.dp)
        )
        Spacer(modifier = Modifier.size(20.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.Bottom) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        Modifier.size(27.dp)
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(text = location)
                }
                Spacer(modifier = Modifier.size(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        Modifier.size(27.dp)
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(text = time)
                }
                Spacer(modifier = Modifier.size(15.dp))
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(end = 25.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    Modifier.size(32.dp)
                )
                Text(text = "${minimumParticipants}명 ~ ${maximumParticipants}명", fontSize = 15.sp)
                Spacer(modifier = Modifier.size(3.dp))
                Text(text = "${currentParticipants}명 참여")
                Spacer(modifier = Modifier.size(15.dp))
            }
        }
        Text(
            text = description,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp)
        )

    }*/
}


@Composable
fun FindGatheringCardList(viewModel: MainViewModel) {
    val posts = viewModel.posts
    viewModel.getPostFromFirestore()
    LazyColumn {
        items(posts) { postData ->
            GatheringCard(
                title = postData.gatheringTitle,
                location = postData.gatheringLocation,
                currentParticipants = "X",
                minimumParticipants = "X",
                maximumParticipants = postData.maximumParticipant,
                time = postData.gatheringTime,
                description = postData.gatheringDescription
            )
        }
    }
}

package hello.kwfriends.ui.screens.findGathering

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hello.kwfriends.firebase.firestoreManager.PostManager
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
    Column(
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
            fontSize = 26.sp,
            fontWeight = FontWeight(760),
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

    }
}


@Composable
fun FindGatheringScreen(viewModel: MainViewModel) {
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

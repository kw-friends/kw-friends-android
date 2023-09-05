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
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CornerSize
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

@Composable
fun GatheringCard(
    title: String,
    location: String,
    currentParticipants: Int,
    minimumParticipants: Int,
    maximumParticipants: Int,
    time: String //추후 datetime으로 변경
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
            modifier = Modifier.padding(start = 20.dp, top = 15.dp)
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
    }
}

@Composable
fun findGatheringScreen() {
    GatheringCard(
        title = "코틀린 스터디 모집합니다!",
        location = "광운대학교 중앙도서관 오픈열람실",
        currentParticipants = 3,
        minimumParticipants = 2,
        maximumParticipants = 6,
        time = "2023-09-10"
    )
}
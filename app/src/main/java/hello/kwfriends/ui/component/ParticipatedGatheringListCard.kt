package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.ui.screens.findGathering.GatheringList
import hello.kwfriends.ui.screens.main.MainViewModel

@Composable
fun ParticipatedGatheringListCard(
    participatedGatherings: List<Pair<PostDetail, ParticipationStatus>>,
    mainViewModel: MainViewModel,
    gotoFindGatheringScreen: (Boolean) -> Unit
) {
    val maximumLines = 2

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8E3E3))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, top = 8.dp)
                .height(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "내가 참여한 모임",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.W600
            )
            if (participatedGatherings.size > maximumLines) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { gotoFindGatheringScreen(true) }
                ) {
                    Text(
                        text = "${participatedGatherings.size - 2}개 더보기",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                    IconButton(
                        onClick = { gotoFindGatheringScreen(true) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "내가 참여한 모임",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            if (participatedGatherings.isEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { gotoFindGatheringScreen(false) }
                ) {
                    Text(
                        text = "모임 찾기",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                    IconButton(
                        onClick = { gotoFindGatheringScreen(false) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "모임 찾기",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 10.dp)
        ) {
            if (participatedGatherings.isEmpty()) {
                Text(
                    text = "참여한 모임이 없습니다.",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp)
                )
            }
            GatheringList(
                posts = participatedGatherings.map { it.first },
                mainViewModel = mainViewModel,
                maximumItems = maximumLines,
                logo = false,
                showParticipationStatus = false,
                showNoSearchResultMessage = false
            )
        }
    }
}
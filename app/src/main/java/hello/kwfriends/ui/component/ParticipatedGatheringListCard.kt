package hello.kwfriends.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
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
    mainViewModel: MainViewModel
) {
    var expended by remember { mutableStateOf(false) }

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
            if (participatedGatherings.isNotEmpty()) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "내가 참여한 모임",
                        modifier = Modifier.size(24.dp)
                    )
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
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            GatheringList(
                posts = participatedGatherings.map { it.first },
                mainViewModel = mainViewModel,
                maximumItems = maximumLines,
                logo = false,
                showParticipationStatus = false
            )
            AnimatedVisibility(expended) {
                GatheringList(
                    posts = participatedGatherings.map { it.first },
                    mainViewModel = mainViewModel,
                    maximumItems = maximumLines,
                    excludeFrontPosts = true,
                    logo = false,
                    showParticipationStatus = false
                )
            }
            AnimatedVisibility(participatedGatherings.size > maximumLines) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFAF3F3))
                    .clickable {
                        expended = !expended
                    }
                    .height(48.dp)
                ) {
                    Text(
                        text = if (expended) "목록 접기" else "더보기",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
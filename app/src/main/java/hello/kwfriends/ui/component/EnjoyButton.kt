package hello.kwfriends.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.PostDetail

@Composable
fun EnjoyButton(
    modifier: Modifier = Modifier,
    postDetail: PostDetail?,
    updateStatus: () -> Unit,
    editPostInfo: () -> Unit
) {
    val participationStatus = postDetail?.myParticipantStatus

    Button(
        modifier = modifier,
        onClick = {
            if (
                participationStatus == ParticipationStatus.PARTICIPATED ||
                participationStatus == ParticipationStatus.NOT_PARTICIPATED
            ) {
                updateStatus()
            }
            if (participationStatus == ParticipationStatus.MY_GATHERING) {
                editPostInfo()
            }
        },
        enabled = ((participationStatus == ParticipationStatus.PARTICIPATED ||
                participationStatus == ParticipationStatus.NOT_PARTICIPATED) &&
                participationStatus != ParticipationStatus.MAXED_OUT) ||
                participationStatus == ParticipationStatus.MY_GATHERING

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentWidth()
                .animateContentSize()
        ) {
            when (participationStatus) {
                ParticipationStatus.PARTICIPATED -> {
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

                ParticipationStatus.MAXED_OUT -> {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = "Maxed out"
                    )
                    Spacer(modifier = Modifier.size(7.dp))
                    Text(
                        text = "인원 가득 참",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                ParticipationStatus.MY_GATHERING -> {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Maxed out"
                    )
                    Spacer(modifier = Modifier.size(7.dp))
                    Text(
                        text = "모임 수정하기",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                else -> { // participationStatus == ParticipationStatus.NOT_PARTICIPATED
                    Text(
                        text = "모임 참여",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

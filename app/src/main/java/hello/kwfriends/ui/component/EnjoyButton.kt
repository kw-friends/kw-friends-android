package hello.kwfriends.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
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

@Composable
fun EnjoyButton(
    modifier: Modifier = Modifier,
    status: ParticipationStatus?,
    updateStatus: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = {
            if (status == ParticipationStatus.PARTICIPATED
                || status == ParticipationStatus.NOT_PARTICIPATED
            ) {
                updateStatus()
            }
        },
        enabled =
        (status == ParticipationStatus.PARTICIPATED || status == ParticipationStatus.NOT_PARTICIPATED) && status != ParticipationStatus.MAXED_OUT

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentWidth()
                .animateContentSize()
        ) {
            when (status) {
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

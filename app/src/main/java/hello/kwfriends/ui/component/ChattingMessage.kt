package hello.kwfriends.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.MessageDetail
import hello.kwfriends.firebase.realtimeDatabase.MessageType
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ChattingImage
import hello.kwfriends.firebase.storage.ProfileImage
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChattingMessage(
    messageDetail: MessageDetail,
    onMessageRemove: () -> Unit,
    onImageClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = ProfileImage.usersUriMap[messageDetail.uid]
                    ?: R.drawable.profile_default_image,
                placeholder = painterResource(id = R.drawable.profile_default_image),
                contentDescription = "chatter's profile image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(0.5.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Column {
                var menuExpanded by remember { mutableStateOf(false) }
                Text(
                    text = UserData.usersDataMap[messageDetail.uid]?.get("name")
                        ?.toString() ?: "unknown"
                )
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        Modifier
                            .clip(
                                RoundedCornerShape(
                                    topEnd = 15.dp,
                                    bottomStart = 15.dp,
                                    bottomEnd = 15.dp
                                )
                            )
                            .background(Color(0xFFE7E4E4))
                    ) {
                        if (messageDetail.uid == Firebase.auth.currentUser!!.uid && messageDetail.type != MessageType.DELETED) {
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "삭제",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    enabled = messageDetail.type != MessageType.LOADING && messageDetail.type != MessageType.DELETED,
                                    onClick = {
                                        menuExpanded = false
                                        onMessageRemove()
                                    },
                                )
                            }
                        }
                        when (messageDetail.type) {
                            MessageType.IMAGE -> {
                                AsyncImage(
                                    model = ChattingImage.chattingUriMap[messageDetail.content],
                                    placeholder = painterResource(id = R.drawable.test_image),
                                    contentDescription = "chatting room image",
                                    modifier = Modifier
                                        .combinedClickable(
                                            onClick = {
                                                onImageClick()
                                            },
                                            onLongClick = { menuExpanded = true }
                                        )
                                        .size(150.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .shadow(500.dp, RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                            MessageType.TEXT -> {
                                Text(
                                    modifier = Modifier
                                        .combinedClickable(
                                            onClick = { },
                                            onLongClick = { menuExpanded = true }
                                        )
                                        .align(Alignment.Center)
                                        .padding(10.dp),
                                    text = messageDetail.content,
                                )
                            }
                            MessageType.DELETED -> {
                                Text(
                                    modifier = Modifier
                                        .combinedClickable(
                                            onClick = { },
                                            onLongClick = { menuExpanded = true }
                                        )
                                        .align(Alignment.Center)
                                        .padding(10.dp),
                                    text = messageDetail.content,
                                    color = Color.Gray
                                )
                            }
                            MessageType.LOADING -> {
                                Text(
                                    modifier = Modifier
                                        .combinedClickable(
                                            onClick = { },
                                            onLongClick = { menuExpanded = true }
                                        )
                                        .align(Alignment.Center)
                                        .padding(10.dp),
                                    text = "로딩중...",
                                    color = Color.Gray
                                )
                            }

                            else -> {}
                        }
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${messageDetail.read.size}명 읽음",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
        val messageDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(messageDetail.timestamp as Long),
            ZoneId.systemDefault()
        ).toLocalDate()
        val today = LocalDate.now()
        Text(
            modifier = Modifier.align(Alignment.TopEnd),
            text = if (messageDate.isEqual(today)) SimpleDateFormat(
                "a hh:mm",
                Locale.getDefault()
            ).format(messageDetail.timestamp)
            else SimpleDateFormat("yyyy/MM/d a hh:mm", Locale.getDefault()).format(
                messageDetail.timestamp
            ),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Preview
@Composable
fun ChattingMessagePreview() {
    ChattingMessage(messageDetail = MessageDetail(), onMessageRemove = { }, onImageClick = {})
}
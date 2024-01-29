package hello.kwfriends.ui.screens.chatting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChattingScreen(
    chattingViewModel: ChattingViewModel,
    navigation: NavController,
    roomID: String
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(true) {
        chattingViewModel.getRoomInfo(roomID)
        chattingViewModel.getMessages(roomID)
        chattingViewModel.getUsersProfile()
        chattingViewModel.addListener(roomID)
    }
    LaunchedEffect(chattingViewModel.messageData) {
        scrollState.scrollTo(scrollState.maxValue)
        Chattings.messageRead(roomID, chattingViewModel.messageData?.toMutableMap() ?: mutableMapOf())
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBFF))
    ) {
        //top start
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navigation.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "back button"
                )
            }
            Text(
                text = Chattings.chattingRoomList?.get(roomID)?.title ?: "",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            //top
            val sortedData = chattingViewModel.messageData?.entries?.sortedBy {
                if(it.value.timestamp.toString() == "") Long.MIN_VALUE
                else it.value.timestamp as Long
            }
            sortedData?.forEach {
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
                            model = ProfileImage.usersUriMap[it.value.uid]
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
                            Text(
                                text = UserData.usersDataMap[it.value.uid]?.get("name")
                                    ?.toString() ?: "unknown"
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Box(
                                    Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topEnd = 10.dp,
                                                bottomStart = 10.dp,
                                                bottomEnd = 10.dp
                                            )
                                        )
                                        .background(Color(0xFFE7E4E4))
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(10.dp),
                                        text = it.value.content
                                    )
                                }
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "${it.value.read.size}명 읽음",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    Text(
                        modifier = Modifier.align(Alignment.TopEnd),
                        text = SimpleDateFormat("a hh:mm", Locale.getDefault()).format(
                            it.value.timestamp
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            TextField(
                value = chattingViewModel.inputChatting,
                onValueChange = { chattingViewModel.setInputChattingText(it) }
            )
            Button(onClick = {
                chattingViewModel.sendMessage(roomID)
            }) {
                Text(text = "전송")
            }
        }

    }
}
package hello.kwfriends.ui.screens.chattingList

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.ChattingRoomType
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.ui.screens.main.Routes
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChattingListScreen(
    chattingsListViewModel: ChattingsListViewModel,
    navigation: NavController
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(true) {
        chattingsListViewModel.getRoomList()
        Chattings.removeChattingListener()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "참가중인 채팅 목록",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.W600,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 14.dp)
        )
        chattingsListViewModel.sortedData?.forEach {
            val roomInfo = it.value
            var targetUid = ""
            if(it.value.type == ChattingRoomType.DIRECT) {
                val temp = roomInfo.members.toMutableMap()
                temp.remove(Firebase.auth.currentUser!!.uid)
                targetUid = temp.keys.toString()
                targetUid = targetUid.slice(IntRange(1, targetUid.length - 2))
            }
            Box(modifier = Modifier
                .clickable { navigation.navigate(Routes.CHATTING_SCREEN + "/${it.key}") }
                .padding(10.dp)
                .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    AsyncImage(
                        model = if(roomInfo.type == ChattingRoomType.DIRECT) {
                                    ProfileImage.usersUriMap[targetUid] ?: R.drawable.profile_default_image
                                }
                                else R.drawable.test_image,
                        placeholder = painterResource(id = R.drawable.test_image),
                        contentDescription = "chatting room's example image",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = if(roomInfo.type == ChattingRoomType.DIRECT) {
                                    (UserData.usersDataMap[targetUid]?.get("name") ?: "unknown").toString()
                                    }
                                    else roomInfo.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,
                                fontWeight = FontWeight(400),
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = (roomInfo.members).size.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Gray,
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = roomInfo.recentMessage.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Text(
                    modifier = Modifier.align(Alignment.TopEnd),
                    text =
                        if(roomInfo.recentMessage.timestamp.toString() == "") ""
                        else SimpleDateFormat(
                            "yyyy/MM/dd hh:mm a",
                            Locale.getDefault()
                        ).format(
                            roomInfo.recentMessage.timestamp
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Divider(
                modifier = Modifier.padding(horizontal = 5.dp),
                color = Color.LightGray,
                thickness = 0.5.dp,
            )
        }
        Button(
            onClick = {
                chattingsListViewModel.temp_addRoom()
                chattingsListViewModel.getRoomList()
            }
        ) {
            Text("채팅방 생성하기")
        }
        Button(
            onClick = {
                chattingsListViewModel.temp_sendMessage()
            }
        ) {
            Text("메세지 전송하기")
        }
    }
}

@Preview
@Composable
fun ChattingListScreenPreview() {
    ChattingListScreen(
        chattingsListViewModel = ChattingsListViewModel(),
        navigation = rememberNavController()
    )
}
package hello.kwfriends.ui.screens.chattingList

import androidx.compose.foundation.background
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
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.ui.screens.main.Routes
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChattingListScreen(
    chattingListViewModel: ChattingListViewModel,
    navigation: NavController
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(true) {
        chattingListViewModel.getRoomList()
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
                onClick = { navigation.navigate(Routes.HOME_SCREEN) }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "back button"
                )
            }
            Text(
                text = "참가중인 채팅 목록",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Default
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            //top
            val sortedData = Chattings.chattingRoomList?.entries?.sortedByDescending {
                ((it.value["recentMessage"] as Map<String, Any?>?)?.get("timestamp") as? Long)
                    ?: Long.MAX_VALUE
            }
            sortedData?.forEach {
                val roomInfo = it.value as Map<String, Any?>?
                val recentMessage = roomInfo?.get("recentMessage") as Map<String, Any?>?
                Box(modifier = Modifier
                    .clickable { navigation.navigate(Routes.CHATTING_SCREEN + "/${it.key}") }
                    .padding(10.dp)
                    .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        AsyncImage(
                            model = R.drawable.test_image,
                            placeholder = painterResource(id = R.drawable.test_image),
                            contentDescription = "chatting room's example image",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = roomInfo?.get("title")?.toString() ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Default,
                                color = Color.Black,
                                fontWeight = FontWeight(400),
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = recentMessage?.get("content")?.toString() ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                fontFamily = FontFamily.Default,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (recentMessage?.get("timestamp") != null) {
                        Text(
                            modifier = Modifier.align(Alignment.TopEnd),
                            text = SimpleDateFormat(
                                "yyyy/MM/dd hh:mm a",
                                Locale.getDefault()
                            ).format(
                                recentMessage["timestamp"]
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Default,
                            color = Color.Gray
                        )
                    }

                }
                Divider(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    color = Color.LightGray,
                    thickness = 0.5.dp,
                )
            }
            Button(
                onClick = {
                    chattingListViewModel.temp_addRoom()
                    chattingListViewModel.getRoomList()
                }
            ) {
                Text("채팅방 생성하기")
            }
            Button(
                onClick = {
                    chattingListViewModel.temp_sendMessage()
                    chattingListViewModel.getRoomList()
                }
            ) {
                Text("메세지 전송하기")
            }
        }
    }
}

@Preview
@Composable
fun ChattingListScreenPreview() {
    ChattingListScreen(
        chattingListViewModel = ChattingListViewModel(),
        navigation = rememberNavController()
    )
}
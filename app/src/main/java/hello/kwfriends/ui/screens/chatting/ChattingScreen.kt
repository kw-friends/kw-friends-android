package hello.kwfriends.ui.screens.chatting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.ui.main.Routes
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
        chattingViewModel.getMessages(roomID)
    }
    LaunchedEffect(chattingViewModel.chattingData) {
        scrollState.scrollTo(scrollState.maxValue)
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
                onClick = { navigation.navigate(Routes.CHATTING_LIST_SCREEN) }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "back button"
                )
            }
            Text(
                text = Chattings.chattingRoomList?.get(roomID)?.get("title").toString(),
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
            val sortedData = chattingViewModel.chattingData?.entries?.sortedBy {
                (it.value["timestamp"] as? Long) ?: Long.MAX_VALUE
            }
            sortedData?.forEach {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        AsyncImage(
                            model = ProfileImage.usersUriMap[it.value["uid"]]
                                ?: R.drawable.profile_default_image,
                            placeholder = painterResource(id = R.drawable.profile_default_image),
                            contentDescription = "chatter's profile image",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(0.5.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                        Column {
                            Text(text = UserData.usersDataMap[it.value["uid"]]?.get("name")?.toString() ?: "unknown")
                            Text(text = it.value["content"].toString())
                        }
                    }
                    Text(
                        modifier = Modifier.align(Alignment.TopEnd),
                        text = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault()).format(
                            it.value["timestamp"]
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Default,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
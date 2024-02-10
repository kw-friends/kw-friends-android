package hello.kwfriends.ui.screens.chatting

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.ChattingRoomType
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.firebase.realtimeDatabase.MessageType
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ChattingImage
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.preferenceDatastore.UserDataStore
import hello.kwfriends.ui.component.ChattingTextField
import hello.kwfriends.ui.screens.main.Routes
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChattingScreen(
    chattingViewModel: ChattingViewModel,
    navigation: NavController,
    roomID: String
) {
    try {
        Firebase.auth.currentUser!!.uid
    }
    catch (e: Exception) {
        navigation.navigate(Routes.HOME_SCREEN)
        Log.w("Chattings screen", "오류 발생, 메인 스크린으로 이동. error: $e")
    }
    val interactionSource = remember { MutableInteractionSource() }
    var targetUid = ""
    if(chattingViewModel.roomInfo?.type == ChattingRoomType.DIRECT) {
        val temp = chattingViewModel.roomInfo?.members?.toMutableMap()
        temp?.remove(Firebase.auth.currentUser!!.uid)
        targetUid = temp?.keys.toString()
        targetUid = targetUid.slice(IntRange(1, targetUid.length - 2))
    }
    //이미지 선택
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.w("Lim", "이미지 선택 완료")
            chattingViewModel.chattingImageUri = uri
        }
    }
    val scrollState = rememberScrollState()
    LaunchedEffect(chattingViewModel.messageData) {
        scrollState.scrollTo(scrollState.maxValue)
        Chattings.messageRead(roomID, chattingViewModel.messageData?.toMutableMap() ?: mutableMapOf())
    }
    LaunchedEffect(true) {
        chattingViewModel.getRoomInfoAndUserProfile(roomID)
    }
    //리스너 생명주기 컴포즈에 맞추기
    DisposableEffect(true) {
        chattingViewModel.addListener(roomID)
        onDispose {
            Chattings.removeMessageListener()
        }
    }
    if (chattingViewModel.imagePopupUri != null) {
        Popup(
            onDismissRequest = { chattingViewModel.imagePopupUri = null }
        ) {
            BackHandler {
                chattingViewModel.imagePopupUri = null
            }
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        chattingViewModel.imagePopupUri = null
                    }
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                AsyncImage(
                    model = ChattingImage.chattingUriMap[chattingViewModel.imagePopupUri],
                    placeholder = painterResource(id = R.drawable.test_image),
                    contentDescription = "chatting room image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(30.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
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
                onClick = {
                    navigation.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "back button"
                )
            }
            Text(
                modifier = Modifier.widthIn(max = 200.dp),
                text = if(chattingViewModel.roomInfo?.type == ChattingRoomType.GROUP) {
                            chattingViewModel.roomInfo?.title ?: "unknwon"
                        }
                        else (UserData.usersDataMap[targetUid]?.get("name") ?: "unknown").toString(),
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = (chattingViewModel.roomInfo?.members?.size).toString(),
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
            )
        }
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { chattingViewModel.showSideSheet = true }
        ) {
            Icon(imageVector = Icons.Default.Dehaze, contentDescription = "chatting room menu button")
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
                if(!UserDataStore.userIgnoreList.contains(it.value.uid)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.TopStart),
                            verticalAlignment = Alignment.Top
                        ) {
                            if (it.value.uid == "BROADCAST") {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 55.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        Modifier
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(Color(0xFFE7E4E4))
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .padding(10.dp),
                                            text = it.value.content
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            } else {
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
                                    var menuExpanded by remember { mutableStateOf(false) }
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
                                                        topEnd = 15.dp,
                                                        bottomStart = 15.dp,
                                                        bottomEnd = 15.dp
                                                    )
                                                )
                                                .background(Color(0xFFE7E4E4))
                                        ) {
                                            if (it.value.uid == Firebase.auth.currentUser!!.uid && it.value.type != MessageType.DELETED) {
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
                                                        enabled = it.value.type != MessageType.LOADING && it.value.type != MessageType.DELETED,
                                                        onClick = {
                                                            menuExpanded = false
                                                            chattingViewModel.removeMessage(
                                                                roomID,
                                                                it.value.messageID
                                                            )
                                                        },
                                                    )
                                                }
                                            }
                                            if(it.value.type == MessageType.IMAGE) {
                                                AsyncImage(
                                                    model = ChattingImage.chattingUriMap[it.value.content],
                                                    placeholder = painterResource(id = R.drawable.test_image),
                                                    contentDescription = "chatting room image",
                                                    modifier = Modifier
                                                        .combinedClickable(
                                                            onClick = {
                                                                chattingViewModel.imagePopupUri =
                                                                    it.value.content
                                                            },
                                                            onLongClick = { menuExpanded = true }
                                                        )
                                                        .size(150.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .shadow(500.dp, RoundedCornerShape(10.dp)),
                                                    contentScale = ContentScale.Crop,
                                                )
                                            }
                                            else if(it.value.type == MessageType.TEXT) {
                                                Text(
                                                    modifier = Modifier
                                                        .combinedClickable(
                                                            onClick = { },
                                                            onLongClick = { menuExpanded = true }
                                                        )
                                                        .align(Alignment.Center)
                                                        .padding(10.dp),
                                                    text = it.value.content,
                                                )
                                            }
                                            else if(it.value.type == MessageType.DELETED) {
                                                Text(
                                                    modifier = Modifier
                                                        .combinedClickable(
                                                            onClick = { },
                                                            onLongClick = { menuExpanded = true }
                                                        )
                                                        .align(Alignment.Center)
                                                        .padding(10.dp),
                                                    text = it.value.content,
                                                    color = Color.Gray
                                                )
                                            }
                                            else if(it.value.type == MessageType.LOADING) {
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
                        }
                        if(it.value.uid != "BROADCAST") {
                            val messageDate = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(it.value.timestamp as Long),
                                ZoneId.systemDefault()
                            ).toLocalDate()
                            val today = LocalDate.now()
                            Text(
                                modifier = Modifier.align(Alignment.TopEnd),
                                text = if (messageDate.isEqual(today)) SimpleDateFormat(
                                    "a hh:mm",
                                    Locale.getDefault()
                                ).format(it.value.timestamp)
                                else SimpleDateFormat("yyyy/MM/d a hh:mm", Locale.getDefault()).format(
                                    it.value.timestamp
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
        if(chattingViewModel.chattingImageUri != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 70.dp),
            ) {
                Surface(
                    modifier = Modifier.padding(end = 20.dp, top = 20.dp),
                    elevation = 15.dp,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = chattingViewModel.chattingImageUri,
                        placeholder = painterResource(id = R.drawable.test_image),
                        contentDescription = "chatting room image",
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shadow(500.dp, RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = {
                        chattingViewModel.chattingImageUri = null
                    }
                ) {
                    Icon(
                        modifier = Modifier.shadow(8.dp, CircleShape),
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "image cancel",
                        tint = Color.Red
                    )
                }
            }
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            ChattingTextField(
                value = chattingViewModel.inputChatting,
                onValueChange = { chattingViewModel.setInputChattingText(it) },
                chattingSend = { chattingViewModel.sendMessage(roomID) },
                imageSelect = {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
        val backgroundColor by animateColorAsState(
            targetValue = if (chattingViewModel.showSideSheet) Color.Black.copy(alpha = 0.7f) else Color.Transparent,
            animationSpec = tween(durationMillis = 300), label = "" // 1초 동안 색상 전환
        )
        // 오른쪽에서 슬라이드하여 나타나는 Side Sheet
        AnimatedVisibility(
            visible = chattingViewModel.showSideSheet,
            enter = slideInHorizontally(
                // 오른쪽에서 시작
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                // 오른쪽으로 사라짐
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { chattingViewModel.showSideSheet = false }
                        .fillMaxHeight()
                        .weight(1f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(330.dp)
                        .background(Color.White)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "대화 상대",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        chattingViewModel.roomInfo?.members?.forEach {
                            Row(
                                modifier = Modifier.padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ProfileImage.usersUriMap[it.key] ?: R.drawable.profile_default_image,
                                    placeholder = painterResource(id = R.drawable.profile_default_image),
                                    contentDescription = "chatter's profile image",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .border(0.5.dp, Color.Gray, CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = UserData.usersDataMap[it.key]?.get("name")?.toString() ?: "unknown",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
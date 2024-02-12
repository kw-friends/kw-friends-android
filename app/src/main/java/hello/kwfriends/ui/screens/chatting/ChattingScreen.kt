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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ChattingImage
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.preferenceDatastore.UserDataStore
import hello.kwfriends.ui.component.ChattingBroadcast
import hello.kwfriends.ui.component.ChattingMessage
import hello.kwfriends.ui.component.ChattingTextField
import hello.kwfriends.ui.screens.main.Routes

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
                if(it.value.uid == "BROADCAST") {
                    ChattingBroadcast(it.value.content)
                }
                else {
                    if(!UserDataStore.userIgnoreList.contains(it.value.uid)) {
                        ChattingMessage(
                            messageDetail = it.value,
                            onMessageRemove = {
                                chattingViewModel.removeMessage(
                                    roomID,
                                    it.value.messageID
                                )
                            },
                            onImageClick = { chattingViewModel.imagePopupUri = it.value.content },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
        //이미지 선택해서 전송 대기 상태
        if(chattingViewModel.chattingImageUri != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 70.dp),
            ) {
                Surface(
                    modifier = Modifier.padding(end = 20.dp, top = 20.dp),
                    shadowElevation = 15.dp,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = chattingViewModel.chattingImageUri,
                        placeholder = painterResource(id = R.drawable.test_image),
                        contentDescription = "chatting room image",
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shadow(10.dp, RoundedCornerShape(10.dp)),
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
                        modifier = Modifier.shadow(3.dp, CircleShape),
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "image cancel",
                        tint = Color.Red
                    )
                }
            }
        }
        ChattingTextField(
            modifier = Modifier.align(Alignment.BottomCenter),
            value = chattingViewModel.inputChatting,
            onValueChange = { chattingViewModel.setInputChattingText(it) },
            chattingSend = { chattingViewModel.sendMessage(roomID) },
            imageSelect = {
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
        //Side Sheet 외부 배경 어둡게 변환
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
            BackHandler {
                chattingViewModel.showSideSheet = false
            }
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
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {}
                        )
                        .fillMaxHeight()
                        .width(290.dp) //side sheet 너비
                        .background(Color.White)
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                    ) {
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
                    if(
                        chattingViewModel.roomInfo?.members?.containsKey(Firebase.auth.currentUser!!.uid) == true
                        && chattingViewModel.roomInfo?.owners?.containsKey(Firebase.auth.currentUser!!.uid) != true
                        && chattingViewModel.roomInfo?.type == ChattingRoomType.GROUP
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .clickable {
                                    navigation.navigate(Routes.HOME_SCREEN)
                                    chattingViewModel.leaveCattingRoom(chattingViewModel.roomInfo!!.roomID)
                                }
                                .padding(10.dp),
                            text = "채팅방 나가기",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
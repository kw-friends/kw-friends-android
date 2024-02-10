package hello.kwfriends.ui.screens.post.postInfo

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.image.ImageLoaderFactory
import hello.kwfriends.ui.component.AnnotatedClickableText
import hello.kwfriends.ui.screens.main.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private val uid = Firebase.auth.currentUser!!.uid

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PostInfoScreen(
    postDetail: PostDetail,
    onDismiss: () -> Unit,
    onPostReport: () -> Unit,
    onPostDelete: () -> Unit,
    mainViewModel: MainViewModel,
    enjoyButton: @Composable () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val previousParticipants = remember { mutableStateOf<MutableList<String>>(mutableListOf()) }

    val context = LocalContext.current
    val imageLoader = ImageLoaderFactory.getInstance(context)

    //모임 참가한 유저들 이미지 및 데이터 가져오기
    LaunchedEffect(postDetail.participants) {
        val newParticipations = postDetail.participants.keys - previousParticipants.value.toSet()
        newParticipations.forEach {
            mainViewModel.downlodUri(it)
            mainViewModel.downlodData(it)

            val request = ImageRequest.Builder(context)
                .data(ProfileImage.usersUriMap[it])
                .build()

            imageLoader.enqueue(request)
            Log.d("imageLoader.enqueue", "Queued $it")
        }
        previousParticipants.value.addAll(postDetail.participants.keys)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBFF)),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "back button"
                        )
                    }
                },
                title = {
                    Text(
                        text = "모임 상세정보",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W600
                    )
                },
                actions = {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "report menu",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "신고",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            enabled = !postDetail.reporters.containsKey(uid) && postDetail.gatheringPromoterUID != uid,
                            onClick = {
                                menuExpanded = false
                                onPostReport()
                            },
                            trailingIcon = {
                                if (postDetail.reporters.containsKey(uid)) {
                                    Icon(
                                        Icons.Default.Check,
                                        tint = Color.Gray,
                                        contentDescription = "check icon"
                                    )
                                }
                            }
                        )
                        if (postDetail.gatheringPromoterUID == Firebase.auth.currentUser!!.uid) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "모임 삭제",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = { onPostDelete() }
                            )
                        }
                    }
                }
            )
        }
    ) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                mainViewModel.userInfoPopupState =
                                    true to postDetail.gatheringPromoterUID
                            }) {
                        AsyncImage(
                            model = ProfileImage.usersUriMap[postDetail.gatheringPromoterUID]
                                ?: R.drawable.profile_default_image,
                            imageLoader = imageLoader,
                            placeholder = painterResource(id = R.drawable.profile_default_image),
                            contentDescription = "gathering promoter's profile image",
                            onLoading = {
                                Log.d(
                                    "AsyncImage",
                                    "Loading: ${ProfileImage.usersUriMap[postDetail.gatheringPromoterUID]}"
                                )
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(0.5.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = UserData.usersDataMap[postDetail.gatheringPromoterUID]?.get("name")
                                ?.toString() ?: "unknown",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight(500)
                        )
                    }
                    Text(
                        text = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(
                            postDetail.timestamp
                        ),
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                    )
                }
                Text(
                    text = postDetail.gatheringTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight(600)
                )
                AnnotatedClickableText(
                    text = postDetail.gatheringDescription,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
            ) {
                FlowRow(modifier = Modifier.padding(top = 20.dp)) {
                    postDetail.gatheringTags.forEach {
                        Text(
                            text = "#${it}",
                            modifier = Modifier.padding(end = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                if (postDetail.gatheringTime != 0L) {
                    Text(
                        text = "마감 기한:  ${mainViewModel.dateTimeFormat(postDetail.gatheringTime)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight(400)
                    )
                } else {
                    Text(
                        text = "마감 기한이 정해지지 않았습니다",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight(400)
                    )
                }

                if (postDetail.gatheringLocation != "") {
                    Text(
                        text = "모임 장소:  ${postDetail.gatheringLocation}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight(400)
                    )
                } else {
                    Text(
                        text = "모임 장소가 정해지지 않았습니다",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight(400)
                    )
                }

                VerticalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 0.5.dp,
                    color = Color.Gray
                )

                Text(
                    text = "참여 인원  ${postDetail.participants.size}/${postDetail.maximumParticipants}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight(400)
                )

                AnimatedVisibility(visible = postDetail.participants.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Spacer(Modifier.height(15.dp))
                        Row(
                            modifier = Modifier
                                .horizontalScroll(scrollState)
                        ) {
                            //참여자 목록
                            postDetail.participants.forEach {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            mainViewModel.userInfoPopupState =
                                                true to it.key
                                        }
                                        .padding(vertical = 5.dp, horizontal = 2.dp)
                                ) {
                                    AsyncImage(
                                        model = ProfileImage.usersUriMap[it.key]
                                            ?: R.drawable.profile_default_image,
                                        imageLoader = imageLoader,
                                        placeholder = painterResource(id = R.drawable.profile_default_image),
                                        onLoading = {
                                            Log.d(
                                                "AsyncImage",
                                                "Loading: ${ProfileImage.usersUriMap[postDetail.gatheringPromoterUID]}"
                                            )
                                        },
                                        contentDescription = "gathering participant's profile image",
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .border(0.5.dp, Color.Gray, CircleShape),
                                        contentScale = ContentScale.Crop,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        modifier = Modifier.width(60.dp),
                                        text = UserData.usersDataMap[it.key]?.get("name")
                                            ?.toString() ?: "unknown",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    if (it.key == postDetail.gatheringPromoterUID)
                                        Text(
                                            text = "모임 주최자",
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    enjoyButton()
                }

                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    }
}

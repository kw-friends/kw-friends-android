package hello.kwfriends.ui.screens.postInfo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.ui.screens.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostInfoScreen(
    postDetail: PostDetail,
    onDismiss: () -> Unit,
    onReport: () -> Unit,
    homeViewModel: HomeViewModel,
    enjoyButton: @Composable () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val previousParticipants = remember { mutableStateOf<MutableList<String>>(mutableListOf()) }

    //모임 참가한 유저들 이미지 및 데이터 가져오기
    LaunchedEffect(postDetail.participants) {
        val newParticipations = postDetail.participants.keys - previousParticipants.value
        newParticipations.forEach {
            homeViewModel.downlodUri(it)
            homeViewModel.downlodData(it)
        }
        previousParticipants.value.addAll(postDetail.participants.keys)
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
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "back button"
                )
            }
            Text(
                text = "모임 상세정보",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Default
            )
        }

        //top end
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopEnd)
                .wrapContentSize(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = { menuExpanded = true }
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = "report menu")
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("신고") },
                    enabled = !postDetail.reporters.containsKey(Firebase.auth.currentUser!!.uid),
                    onClick = {
                        menuExpanded = false
                        onReport()
                    },
                    trailingIcon = {
                        if (postDetail.reporters.containsKey(Firebase.auth.currentUser!!.uid)) {
                            Icon(
                                Icons.Default.Check,
                                tint = Color.Gray,
                                contentDescription = "check icon"
                            )
                        }
                    }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp, bottom = 40.dp, start = 20.dp, end = 20.dp)
        ) {
            //top
            Row {
                AsyncImage(
                    model = ProfileImage.usersUriMap[postDetail.gatheringPromoterUID]
                        ?: R.drawable.profile_default_image,
                    placeholder = painterResource(id = R.drawable.profile_default_image),
                    contentDescription = "gathering promoter's profile image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(0.5.dp, Color.Gray, CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = UserData.usersDataMap[postDetail.gatheringPromoterUID]?.get("name")
                            ?.toString() ?: "unknown",
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight(500)
                    )
                    Text(
                        text = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(
                            postDetail.timestamp
                        ),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Default,
                        color = Color.Gray
                    )
                }
            }
            Spacer(Modifier.height(15.dp))
            Text(
                text = postDetail.gatheringTitle,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight(600)
            )
            Spacer(Modifier.height(15.dp))
            Text(
                text = postDetail.gatheringDescription,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Default
            )
            FlowRow(modifier = Modifier.padding(top = 20.dp)) {
                postDetail.gatheringTags.forEach {
                    Text(
                        text = "#${it}",
                        modifier = Modifier.padding(end = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Default,
                        color = Color.Gray
                    )
                }
            }

            //bottom
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Divider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    color = Color.Gray,
                    thickness = 0.5.dp,
                )
                Text(
                    text = "참여 인원  ${postDetail.participants.size}/${postDetail.maximumParticipants}",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Default,
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
                                    modifier = Modifier.padding(end = 15.dp)
                                ) {
                                    AsyncImage(
                                        model = ProfileImage.usersUriMap[it.key]
                                            ?: R.drawable.profile_default_image,
                                        placeholder = painterResource(id = R.drawable.profile_default_image),
                                        contentDescription = "gathering participant's profile image",
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .border(0.5.dp, Color.Gray, CircleShape),
                                        contentScale = ContentScale.Crop,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = UserData.usersDataMap[it.key]?.get("name")
                                            ?.toString() ?: "unknown",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Default,
                                    )
                                    if (it.key == postDetail.gatheringPromoterUID)
                                        Text(
                                            text = "모임 주최자",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Default,
                                        )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(30.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    enjoyButton()
                }

            }
        }
    }
}
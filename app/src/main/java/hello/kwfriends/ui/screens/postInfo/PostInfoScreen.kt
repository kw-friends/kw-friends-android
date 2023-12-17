package hello.kwfriends.ui.screens.postInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.PostDetail

@Composable
fun PostInfoScreen(
    postDetail: PostDetail,
    participantsCount: Int,
    onDismiss: () -> Unit,
    onReport: () -> Unit,
    enjoyButton: @Composable () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

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
                    onClick = {
                        menuExpanded = false
                        onReport()
                    },
//                        leadingIcon = {
//                            Icon(
//                                Icons.Outlined.Details,
//                                contentDescription = null
//                            )
//                        },
                    //trailingIcon = { Text("F11", textAlign = TextAlign.Center) }
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
                    model = R.drawable.test_image,
                    placeholder = painterResource(id = R.drawable.profile_default_image),
                    contentDescription = "My profile image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = postDetail.gatheringPromoter,
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight(500)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "n분전",
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
            Row(modifier = Modifier.padding(top = 20.dp)) {
                postDetail.gatheringTags.forEach {
                    Text(
                        text = "#${it}",
                        modifier = Modifier.padding(end = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "참여 인원  ${postDetail.participants.size}/${postDetail.maximumParticipants}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight(400)
                    )
                    Spacer(Modifier.height(15.dp))
                    Row {
                        //참여자 목록
                        postDetail.participants.forEach {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(end = 15.dp)
                            ) {
                                AsyncImage(
                                    model = R.drawable.test_image,
                                    placeholder = painterResource(id = R.drawable.profile_default_image),
                                    contentDescription = "My profile image",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = it.value.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Default,
                                )
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
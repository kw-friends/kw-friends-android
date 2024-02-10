package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.image.ImageLoaderFactory
import hello.kwfriends.preferenceDatastore.UserDataStore

@Composable
fun UserInfoPopup(
    state: Boolean,
    uid: String,
    addUserIgnore: () -> Unit,
    removeUserIgnore: () -> Unit,
    onDismiss: () -> Unit,
    onUserReport: () -> Unit,
    makeDirectChatting: () -> Unit
) {
    val imageLoader = ImageLoaderFactory.getInstance(LocalContext.current)

    if (state) {
        var menuExpanded by remember { mutableStateOf(false) }
        var position by remember { mutableStateOf(Offset.Zero) }
        val myUid = Firebase.auth.currentUser!!.uid
        Popup(
            onDismissRequest = onDismiss,
            alignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .padding(15.dp)

                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(24.dp))

            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { menuExpanded = true },
                ) {
                    Icon(
                        modifier = Modifier
                            .size(23.dp)
                            .onGloballyPositioned { coordinates ->
                                // 버튼의 위치를 저장
                                position = coordinates.positionInRoot()
                            },
                        imageVector = Icons.Default.MoreVert,
                        tint = Color.Gray,
                        contentDescription = "report menu"
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    offset = DpOffset(x = position.x.dp, y = position.y.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("개인 메세지 보내기")
                        },
                        enabled = uid != myUid,
                        onClick = {
                            menuExpanded = false
                            makeDirectChatting()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("신고") },
                        enabled = myUid !in UserData.usersDataMap[uid]?.get(
                            "reporters"
                        ).toString()
                                && uid != myUid,
                        onClick = {
                            menuExpanded = false
                            onUserReport()
                        },
                        trailingIcon = {
                            if (myUid in UserData.usersDataMap[uid]?.get("reporters")
                                    .toString()
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    tint = Color.Gray,
                                    contentDescription = "check icon"
                                )
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            if (uid in UserDataStore.userIgnoreList) Text("차단 해제")
                            else Text("차단")
                        },
                        enabled = uid != myUid,
                        onClick = {
                            menuExpanded = false
                            if (uid in UserDataStore.userIgnoreList) removeUserIgnore()
                            else addUserIgnore()
                        },
                    )
                }

                Row(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp)
                ) {
                    AsyncImage(
                        model = ProfileImage.usersUriMap[uid]
                            ?: R.drawable.profile_default_image,
                        imageLoader = imageLoader,
                        placeholder = painterResource(id = R.drawable.profile_default_image),
                        contentDescription = "gathering participant's profile image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(0.5.dp, Color.Gray, CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Column {
                        Text(
                            text = UserData.usersDataMap[uid]?.get("name")?.toString() ?: "",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight(500),
                        )
                        Text(
                            text = UserData.usersDataMap[uid]?.get("department")?.toString() + " "
                                    + (UserData.usersDataMap[uid]?.get("std-num")?.toString()
                                ?.slice(2..3) ?: "") + "학번",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                        )
                        Text(
                            text = "mbti: " + UserData.usersDataMap[uid]?.get("mbti")?.toString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}
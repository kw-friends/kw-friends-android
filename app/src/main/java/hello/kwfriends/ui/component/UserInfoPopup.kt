package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage

@Composable
fun UserInfoPopup(
    state: Boolean,
    uid: String,
    onDismiss: () -> Unit
) {
    if(state) {
        var menuExpanded by remember { mutableStateOf(false) }
        var buttonPosition by remember { mutableStateOf(Offset.Zero) }
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = onDismiss,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
            ) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .onGloballyPositioned { coordinates ->
                            // 버튼의 위치를 저장
                            buttonPosition = coordinates.positionInRoot()
                        },
                    onClick = { menuExpanded = true }
                ) {
                    Icon(
                        modifier = Modifier.size(23.dp),
                        imageVector = Icons.Default.MoreVert,
                        tint = Color.Gray,
                        contentDescription = "report menu"
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    offset = DpOffset(buttonPosition.x.dp, buttonPosition.y.dp + 50.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("신고") },
                        enabled = true,
                        onClick = {
                            menuExpanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("차단") },
                        enabled = true,
                        onClick = {
                            menuExpanded = false
                        },
                    )
                }
                Row(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp)
                ) {
                    AsyncImage(
                        model = ProfileImage.usersUriMap[uid]
                            ?: R.drawable.profile_default_image,
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
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight(500),
                            fontFamily = FontFamily.Default,
                        )
                        Text(
                            text = UserData.usersDataMap[uid]?.get("department")?.toString() + " "
                                    + (UserData.usersDataMap[uid]?.get("std-num")?.toString()?.slice(2..3) ?: "") + "학번",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Default,
                        )
                        Text(
                            text = "mbti: " + UserData.usersDataMap[uid]?.get("mbti")?.toString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Default,
                        )
                    }
                }
            }
        }
    }
}
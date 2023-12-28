package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.preferenceDatastore.UserDataStore

@Composable
fun UserIgnoreListPopup(
    state: Boolean,
    onDismiss: () -> Unit,
    downloadUri: (String) -> Unit,
    downloadData: (String) -> Unit,
    removeUserIgnore: (String) -> Unit,
    onUserInfoPopup: (String) -> Unit
) {
    if(state) {
        LaunchedEffect(true) {
            UserDataStore.userIgnoreList.forEach {
                downloadUri(it)
                downloadData(it)
            }
        }
        Popup(
            onDismissRequest = onDismiss
        ) {
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
                        text = "차단 유저 목록",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Default
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, bottom = 40.dp, start = 20.dp, end = 20.dp)
                ) {
                    //top
                    UserDataStore.userIgnoreList.forEach { uid ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onUserInfoPopup(uid) }
                            ) {
                                AsyncImage(
                                    model = ProfileImage.usersUriMap[uid]
                                        ?: R.drawable.profile_default_image,
                                    placeholder = painterResource(id = R.drawable.profile_default_image),
                                    contentDescription = "gathering promoter's profile image",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .border(0.5.dp, Color.Gray, CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                                Spacer(modifier = Modifier.width(7.dp))
                                Text(
                                    text = UserData.usersDataMap[uid]?.get("name")
                                        ?.toString() ?: "unknown",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight(500)
                                )
                            }
                            Text(text = "차단 해제",
                                Modifier
                                    .align(Alignment.CenterEnd)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable{ removeUserIgnore(uid) }
                            )
                        }
                    }
                }
            }
        }
    }
}
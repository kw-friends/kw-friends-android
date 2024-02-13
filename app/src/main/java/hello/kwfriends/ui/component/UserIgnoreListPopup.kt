package hello.kwfriends.ui.component

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.image.ImageLoaderFactory
import hello.kwfriends.preferenceDatastore.UserDataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserIgnoreListPopup(
    state: Boolean,
    onDismiss: () -> Unit,
    downloadUri: (String) -> Unit,
    downloadData: (String) -> Unit,
    removeUserIgnore: (String) -> Unit,
    onUserInfoPopup: (String) -> Unit
) {
    val context = LocalContext.current
    val imageLoader = ImageLoaderFactory.getInstance(context)

    if (state) {
        LaunchedEffect(true) {
            UserDataStore.userIgnoreList.forEach {
                downloadUri(it)
                downloadData(it)
            }
        }
        Popup(
            onDismissRequest = onDismiss
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "차단 유저 목록",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { onDismiss() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew,
                                    contentDescription = "back button",
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
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
                                    imageLoader = imageLoader,
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
                                    .clickable { removeUserIgnore(uid) }
                            )
                        }
                    }
                }
            }
        }
    }
}
package hello.kwfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = onDismiss,
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                    .padding(vertical = 16.dp, horizontal = 20.dp)
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
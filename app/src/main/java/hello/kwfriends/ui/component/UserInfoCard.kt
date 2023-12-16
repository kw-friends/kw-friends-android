package hello.kwfriends.ui.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.ui.screens.settings.SettingsViewModel

@Composable
fun UserInfoCard(
    profileImageUri: Uri?,
    userName: String,
    admissionYear: String,
    major: String,
    navigation: NavController,
    settingsViewModel: SettingsViewModel
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 24.dp, bottomStart = 24.dp))
            .background(Color.Transparent)
            .border(1.dp, Color.LightGray, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 24.dp, bottomStart = 24.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            AsyncImage(
                model = profileImageUri ?: R.drawable.profile_default_image,
                placeholder = painterResource(id = R.drawable.profile_default_image),
                contentDescription = "My profile image",
                modifier = Modifier
                    .size(70.dp)
                    .padding(4.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight(700),
                fontFamily = FontFamily.Default,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Transparent)
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                .padding(horizontal = 25.dp)
                .height(IntrinsicSize.Min)
        ) {
            Text(text = major, style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Default)
            Divider(
                color = Color.DarkGray,
                modifier = Modifier
                    .height(18.dp)
                    .padding(horizontal = 6.dp)
                    .width(1.dp)
            )
            Text(text = "${admissionYear}학번", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Default)
            Box(modifier = Modifier.fillMaxSize().padding(vertical = 10.dp)) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .align(Alignment.CenterEnd)
                        .clickable {
                            settingsViewModel.editUserInfo(navigation)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(text = "내 정보 수정", style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Default)
                }
            }

        }
    }
}

@Preview
@Composable
fun UserInfoCardPreview() {
    val navController = rememberNavController()
    UserInfoCard(
        profileImageUri = null,
        userName = "Preview",
        admissionYear = "23",
        major = "소프트웨어학부",
        navigation = navController,
        settingsViewModel = SettingsViewModel()
    )
}
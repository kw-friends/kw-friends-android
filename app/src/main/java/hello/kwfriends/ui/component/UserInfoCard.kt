package hello.kwfriends.ui.component

import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.image.ImageLoaderFactory
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
    val context = LocalContext.current
    val imageLoader = ImageLoaderFactory.getInstance(context)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            AsyncImage(
                model = profileImageUri ?: R.drawable.profile_default_image,
                placeholder = painterResource(id = R.drawable.profile_default_image),
                imageLoader = imageLoader,
                onLoading = { Log.d("AsyncImage", "Loading: $profileImageUri") },
                contentDescription = "My profile image",
                modifier = Modifier
                    .size(72.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .border(0.5.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight(700),
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Transparent)
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .height(IntrinsicSize.Min)
        ) {
            Text(text = major, style = MaterialTheme.typography.labelMedium)
            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .width(1.dp)
                    .height(16.dp),
                color = Color.LightGray
            )
            Text(text = "${admissionYear}학번", style = MaterialTheme.typography.labelMedium)
            Box(modifier = Modifier.fillMaxSize()) {
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
                    Text(text = "내 정보 수정", style = MaterialTheme.typography.bodyMedium)
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
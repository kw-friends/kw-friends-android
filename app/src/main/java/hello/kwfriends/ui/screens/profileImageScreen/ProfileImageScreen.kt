package hello.kwfriends.ui.screens.profileImageScreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.firebase.storageManager.ProfileImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageScreen(navigation: NavController, profileImageViewModel: ProfileImageViewModel) {
    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        ProfileImage.myImageUri = uri
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "프로필 이미지 설정",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(5.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE2A39B)
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navigation.popBackStack() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go to back",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AsyncImage(
                // Use a default image resource
                model = ProfileImage.myImageUri ?: R.drawable.profile_default_image,
                contentDescription = "my profile image",
                modifier = Modifier
                    .padding(4.dp)
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            Button(
                modifier = Modifier.padding(top = 25.dp),
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text("이미지 선택")
            }
            Button(
                modifier = Modifier.padding(top = 10.dp),
                onClick = {
                    profileImageViewModel.imageUpload()
                }
            ) {
                Text("이미지 업로드")
            }
            Button(
                modifier = Modifier.padding(top = 10.dp),
                onClick = {
                    profileImageViewModel.imageLoad()
                }
            ) {
                Text("이미지 로드")
            }
        }
    }
}

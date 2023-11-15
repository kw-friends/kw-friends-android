package hello.kwfriends.ui.screens.settings


import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hello.kwfriends.BuildConfig
import hello.kwfriends.firebase.storageManager.ProfileImage
import hello.kwfriends.ui.component.SettingsButtonItem
import hello.kwfriends.ui.component.SettingsSwitchItem
import hello.kwfriends.ui.component.UserInfoCard
import hello.kwfriends.ui.screens.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navigation: NavController
) {
    val scrollState = rememberScrollState()
    val launcher = rememberLauncherForActivityResult(contract =
        ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            ProfileImage.myImageUri = uri
            if(uri != null){
                Log.w("Lim", "이미지 선택 완료")
                settingsViewModel.profileImageUpload(uri)
            }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "설정",
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
                            contentDescription = "Go to HomeScreen",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            )
        }
    ) {
        Column(
            Modifier.padding(it)
        ) {
            UserInfoCard(
                userName = "!23",
                admissionYear = 23,
                major = "소프트웨어학부",
                grade = 1,
                navigation = navigation
            )
            SettingsSwitchItem(title = "다크 모드", checked = true)
            SettingsSwitchItem(
                title = "조용 모드",
                checked = false,
                description = "모든 알림을 꺼 다른 일에 집중할 수 있어요"
            )
            SettingsSwitchItem(
                title = "라면에 식초 한숟갈?",
                checked = true,
                description = "어승경만 아는 라면 레시피, 절대 실패할 일 없어요. 진짜에요!"
            )
            SettingsButtonItem(
                title = "로그아웃",
                onClick = {}
            )
            SettingsButtonItem(
                title = "회원탈퇴",
                onClick = {}
            )

        }
    }
}


@Composable
fun SettingsButtonItem(
    title: String,
    description: String = "",
    onClick: () -> Unit
) {
    Divider(
        color = Color(0xFF353535),
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clickable { onClick() }
            .clip(shape = AbsoluteRoundedCornerShape(10.dp))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(10F)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                if (description != "") {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Icon(
                profileImageUri = ProfileImage.myImageUri,
                userName = AuthViewModel.userInfo!!["name"]!!.toString(),
                admissionYear = AuthViewModel.userInfo!!["std-num"]!!.toString().slice(IntRange(2, 3)),
                major = AuthViewModel.userInfo!!["department"]!!.toString(),
                navigation = navigation,
                settingsViewModel = settingsViewModel
            )
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Spacer(modifier = Modifier.height(4.dp))
                SettingsButtonItem(
                    title = "프로필 이미지 선택",
                    onClick = {
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    isUpperLine = false
                )
                SettingsSwitchItem(
                    title = "다크 모드",
                    checked = settingsViewModel.isDarkMode,
                    onCheckedChange = { settingsViewModel.isDarkMode = !settingsViewModel.isDarkMode }
                )
                SettingsSwitchItem(
                    title = "조용 모드",
                    checked = settingsViewModel.isQuietMode,
                    onCheckedChange = { settingsViewModel.isQuietMode = !settingsViewModel.isQuietMode },
                    description = "모든 알림을 꺼 다른 일에 집중할 수 있어요"
                )
                SettingsButtonItem(
                    title = "공지사항",
                    onClick = {  }
                )
                SettingsButtonItem(
                    title = "문의하기",
                    onClick = {  }
                )
                SettingsButtonItem(
                    title = "이용규칙",
                    onClick = {  }
                )
                SettingsButtonItem(
                    title = "비밀번호 재설정",
                    onClick = { settingsViewModel.mainFindPassword(navigation) }
                )
                SettingsButtonItem(
                    title = "로그아웃",
                    onClick = { settingsViewModel.mainSignOut(navigation) }
                )
                SettingsButtonItem(
                    title = "회원탈퇴",
                    onClick = { settingsViewModel.mainDeleteUser(navigation) }
                )

                SettingsButtonItem(
                    title = "앱 버전",
                    description = BuildConfig.VERSION_NAME,
                    onClick = { /*앱스토어와 연결*/  }
                )
            }
        }
    }
}



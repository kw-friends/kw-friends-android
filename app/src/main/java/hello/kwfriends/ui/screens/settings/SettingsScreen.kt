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
import hello.kwfriends.firebase.storage.ProfileImage
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
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.w("Lim", "이미지 선택 완료")
            ProfileImage.myImageUri = uri
            settingsViewModel.profileImageUpload(uri)
        }
    }
    //USER_DATA DataStore 객체 저장
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
                profileImageUri = ProfileImage.myImageUri,
                userName = AuthViewModel.userInfo!!["name"]!!.toString(),
                admissionYear = AuthViewModel.userInfo!!["std-num"]!!.toString()
                    .slice(IntRange(2, 3)),
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
                    checked = settingsViewModel.isDarkMode!!,
                    onCheckedChange = { settingsViewModel.switchDarkMode() }
                )
                SettingsSwitchItem(
                    title = "조용 모드",
                    checked = settingsViewModel.isQuietMode!!,
                    onCheckedChange = { settingsViewModel.switchQuietMode() },
                    description = "모든 알림을 꺼 다른 일에 집중할 수 있어요"
                )
                SettingsButtonItem(
                    title = "공지사항",
                    onClick = { }
                )
                SettingsButtonItem(
                    title = "문의하기",
                    onClick = { }
                )
                SettingsButtonItem(
                    title = "이용규칙",
                    onClick = { }
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
                    onClick = { /*앱스토어와 연결*/ }
                )
            }
        }
    }
}
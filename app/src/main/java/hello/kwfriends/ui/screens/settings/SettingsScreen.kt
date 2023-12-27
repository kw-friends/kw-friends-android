package hello.kwfriends.ui.screens.settings


import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hello.kwfriends.BuildConfig
import hello.kwfriends.firebase.authentication.UserAuth
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.preferenceDatastore.UserDataStore
import hello.kwfriends.ui.component.SettingsButtonItem
import hello.kwfriends.ui.component.SettingsSwitchItem
import hello.kwfriends.ui.component.UserIgnoreListPopup
import hello.kwfriends.ui.component.UserInfoCard
import hello.kwfriends.ui.component.UserInfoPopup
import hello.kwfriends.ui.component.UserReportDialog
import hello.kwfriends.ui.main.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navigation: NavController
) {
    val scrollState = rememberScrollState()
    //이미지 선택
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.w("Lim", "이미지 선택 완료")
            ProfileImage.updateUsersUriMap(UserAuth.fa.currentUser?.uid ?: "", uri)
            settingsViewModel.myProfileImageUpload(uri)
        }
    }
    BackHandler {
        if (settingsViewModel.userInfoPopupState.first) {
            settingsViewModel.userInfoPopupState = false to ""
        }
        else if(settingsViewModel.userIgnoreListPopup) {
            settingsViewModel.userIgnoreListPopup = false
        }
        else {
            navigation.navigate(Routes.HOME_SCREEN)
        }
    }
    //프로필 이미지 로드
    if(!settingsViewModel.myProfileImiageLoaded) {
        settingsViewModel.myProfileImageDownload()
    }
    //유저 신고 다이얼로그
    UserReportDialog(
        state = settingsViewModel.userReportDialogState.first,
        textList = settingsViewModel.userReportTextList,
        onDismiss = { settingsViewModel.userReportDialogState = false to "" },
        onUserReport = { settingsViewModel.userReport(it) }
    )
    //유저 정보 팝업
    UserInfoPopup(
        state = settingsViewModel.userInfoPopupState.first,
        uid = settingsViewModel.userInfoPopupState.second,
        addUserIgnore = { settingsViewModel.addUserIgnore(settingsViewModel.userInfoPopupState.second) },
        removeUserIgnore = { settingsViewModel.removeUserIgnore(settingsViewModel.userInfoPopupState.second) },
        onDismiss = { settingsViewModel.userInfoPopupState = false to "" },
        onUserReport = { settingsViewModel.userReportDialogState = true to settingsViewModel.userInfoPopupState.second }
    )
    //유저 차단 목록 팝업
    UserIgnoreListPopup(
        state = settingsViewModel.userIgnoreListPopup,
        onDismiss = { settingsViewModel.userIgnoreListPopup = false },
        downloadUri = { settingsViewModel.downlodUri(it) },
        downloadData = { settingsViewModel.downlodData(it) },
        removeUserIgnore = { settingsViewModel.removeUserIgnore(it) },
        onUserInfoPopup = { settingsViewModel.userInfoPopupState = true to it }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBFF))
    ) {
        Box {
            //top start
            Row(
                modifier = Modifier.align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navigation.navigate(Routes.HOME_SCREEN) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "back button"
                    )
                }
                Text(
                    text = "설정",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Default
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = 60.dp, start = 10.dp, end = 10.dp)
                    .verticalScroll(scrollState)
            ) {
                UserInfoCard(
                    profileImageUri = ProfileImage.usersUriMap[UserAuth.fa.currentUser?.uid],
                    userName = UserData.myInfo!!["name"]!!.toString(),
                    admissionYear = UserData.myInfo!!["std-num"]!!.toString()
                        .slice(IntRange(2, 3)),
                    major = UserData.myInfo!!["department"]!!.toString(),
                    navigation = navigation,
                    settingsViewModel = settingsViewModel
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column {
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
                        checked = UserDataStore.isDarkMode!!,
                        onCheckedChange = { settingsViewModel.switchDarkMode() }
                    )
                    SettingsSwitchItem(
                        title = "조용 모드",
                        checked = UserDataStore.isQuietMode!!,
                        onCheckedChange = { settingsViewModel.switchQuietMode() },
                        description = "모든 알림을 꺼 다른 일에 집중할 수 있어요"
                    )
                    SettingsButtonItem(
                        title = "차단 유저 목록",
                        onClick = { settingsViewModel.userIgnoreListPopup = true }
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

}
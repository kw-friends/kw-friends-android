package hello.kwfriends.ui.screens.settings


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.BuildConfig
import hello.kwfriends.firebase.authentication.UserAuth
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.preferenceDatastore.UserDataStore
import hello.kwfriends.ui.component.SettingsButtonItem
import hello.kwfriends.ui.component.SettingsSwitchItem
import hello.kwfriends.ui.component.UserInfoCard
import hello.kwfriends.ui.component.UserInfoPopup
import hello.kwfriends.ui.component.UserReportDialog
import hello.kwfriends.ui.component.dateTimePicker.CommunityGuidelinePopup
import hello.kwfriends.ui.screens.main.Routes
import hello.kwfriends.ui.screens.settings.notice.NoticePopup
import hello.kwfriends.ui.screens.userIgnoreList.UserIgnoreListPopup

private val mailContext = """

********** 사용자 정보 **********
UID = ${Firebase.auth.currentUser!!.uid}

"""

fun Context.sendMail(to: String, subject: String, context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, mailContext)
        }
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "오류가 발생했습니다. sksmstmdrud@gmail.com 으로 메일을 보내주세요.",
            Toast.LENGTH_LONG
        ).show()
    } catch (t: Throwable) {
        Toast.makeText(
            context,
            "오류가 발생했습니다. sksmstmdrud@gmail.com 으로 메일을 보내주세요.",
            Toast.LENGTH_LONG
        ).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    mainNavigation: NavController
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
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
        } else if (settingsViewModel.userIgnoreListPopup) {
            settingsViewModel.userIgnoreListPopup = false
        } else {
            mainNavigation.navigate(Routes.HOME_SCREEN)
        }
    }
    //프로필 이미지 로드
    if (!settingsViewModel.myProfileImiageLoaded) {
        settingsViewModel.myProfileImageDownload()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBFF)),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "설정",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.W600
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { mainNavigation.navigate(Routes.HOME_SCREEN) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "back button",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
            )
        }
    ) { it ->
        //유저 신고 다이얼로그
        Column(
            modifier = Modifier.padding(it)
        ) {
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
                onUserReport = {
                    settingsViewModel.userReportDialogState =
                        true to settingsViewModel.userInfoPopupState.second
                }
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

            // 커뮤니티 가이드라인 팝업
            CommunityGuidelinePopup(
                state = settingsViewModel.communityGuidelinePopupState,
                onDismiss = { settingsViewModel.communityGuidelinePopupState = false }
            )

            // 공지사항 팝업
            NoticePopup(
                state = settingsViewModel.noticePopupState,
                onDismiss = { settingsViewModel.noticePopupState = false },
                settingsViewModel = settingsViewModel
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .verticalScroll(scrollState)
            ) {
                UserInfoCard(
                    profileImageUri = ProfileImage.usersUriMap[UserAuth.fa.currentUser?.uid],
                    userName = UserData.myInfo!!["name"]!!.toString(),
                    admissionYear = UserData.myInfo!!["std-num"]!!.toString()
                        .slice(IntRange(2, 3)),
                    major = UserData.myInfo!!["department"]!!.toString(),
                    navigation = mainNavigation,
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
                        onClick = { settingsViewModel.noticePopupState = true }
                    )
                    SettingsButtonItem(
                        title = "문의하기",
                        onClick = {
                            context.sendMail(
                                to = "sksmstmdrud@gmail.com",
                                subject = "KW Friends 관련 문의",
                                context = context
                            )
                        }
                    )
                    SettingsButtonItem(
                        title = "이용규칙",
                        onClick = { settingsViewModel.communityGuidelinePopupState = true }
                    )
                    SettingsButtonItem(
                        title = "비밀번호 재설정",
                        onClick = { settingsViewModel.mainFindPassword(mainNavigation) }
                    )
                    SettingsButtonItem(
                        title = "로그아웃",
                        onClick = { settingsViewModel.mainSignOut(mainNavigation) }
                    )
                    SettingsButtonItem(
                        title = "회원탈퇴",
                        onClick = { settingsViewModel.mainDeleteUser(mainNavigation) }
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
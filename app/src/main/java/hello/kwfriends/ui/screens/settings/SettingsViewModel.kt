package hello.kwfriends.ui.screens.settings

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.firebase.realtimeDatabase.Notice
import hello.kwfriends.firebase.realtimeDatabase.NoticeDetail
import hello.kwfriends.firebase.realtimeDatabase.Report
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.preferenceDatastore.UserDataStore
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.ui.screens.auth.AuthUiState
import hello.kwfriends.ui.screens.auth.AuthViewModel
import hello.kwfriends.ui.screens.main.Routes
import kotlinx.coroutines.launch

class SettingsViewModel: ViewModel() {
    //유저 설정 불러왔는지 여부
    var userSettingValuesLoaded by mutableStateOf<Boolean>(false)

    //유저 프로필 불러왔는지 여부
    var myProfileImiageLoaded by mutableStateOf<Boolean>(false)

    //유저 차단 목록 팝업 보이기 여부
    var userIgnoreListPopup by mutableStateOf<Boolean>(false)

    //포스트 팝업 보이기 여부 및 포스트 uid
    var userInfoPopupState by mutableStateOf<Pair<Boolean, String>>(false to "")

    //유저 신고 팝업 보이기 여부 및 신고 대상 uid
    var userReportDialogState by mutableStateOf<Pair<Boolean, String>>(false to "")

    // 커뮤니티 가이드라인 팝업 여부
    var communityGuidelinePopupState by mutableStateOf(false)

    // 공지사항 로딩 완료 여부
    var noticeLoadedState by mutableStateOf(false)

    // 공지사항 팝업 여부
    var noticePopupState by mutableStateOf(false)

    // 공지사항 목록
    var noticeLists by mutableStateOf<List<NoticeDetail>>(listOf())

    //유저 신고 텍스트 리스트
    val userReportTextList by mutableStateOf(
        listOf(
            "낚시/놀람/도배",
            "음란물/불건전한 만남 및 대화",
            "불쾌감을 주는 사용자",
            "정당/정치인 비하 및 선거운동",
            "유출/사칭/사기",
            "상업적 광고 및 판매",
            "욕설/비하"
        )
    )

    //유저 신고 함수
    fun userReport(reason: List<String>) {
        viewModelScope.launch {
            userReportDialogState = false to userReportDialogState.second
            Report.userReport(
                uid = userReportDialogState.second,
                reporterID = Firebase.auth.currentUser?.uid?:"unknown",
                reason = reason,
            )
            downlodData(userReportDialogState.second)
            userReportDialogState = false to ""
        }
    }

    //유저 설정 세팅값들 불러오는 함수
    fun userSettingValuesLoad(){
        viewModelScope.launch {
            UserDataStore.getDataFlow().collect {
                UserDataStore.isDarkMode = it[booleanPreferencesKey("SETTINGS_DARK_MODE")]
                UserDataStore.isQuietMode = it[booleanPreferencesKey("SETTINGS_QUIET_MODE")]
                UserDataStore.userIgnoreList = it[stringSetPreferencesKey("USER_IGNORE_LIST")]?.toMutableSet() ?: mutableSetOf()
                //유저 설정 기본값
                if(UserDataStore.isDarkMode == null) UserDataStore.isDarkMode = false
                if(UserDataStore.isQuietMode == null) UserDataStore.isQuietMode = false
            }
        }
    }

    //다크모드 스위치 변경 함수
    fun switchDarkMode(){
        viewModelScope.launch {
            UserDataStore.setBooleanData("SETTINGS_DARK_MODE", !(UserDataStore.isDarkMode ?: false))
        }
    }

    //조용모드 스위치 변경 함수
    fun switchQuietMode(){
        viewModelScope.launch {
            UserDataStore.setBooleanData("SETTINGS_QUIET_MODE", !(UserDataStore.isQuietMode ?: false))
        }
    }

    // 공지사항 fetch 함수
    fun initNoitces() {
        viewModelScope.launch {
            if (noticeLists.isNotEmpty()) {
                noticeLists = emptyList()
            }
            noticeLists = Notice.initNotices()

            if (noticeLists.isNotEmpty()) {
                noticeLoadedState = true
            }
        }
    }


    //자신의 프로필 이미지를 업로드함
    fun myProfileImageUpload(uri: Uri){
        viewModelScope.launch {
            Log.w("Lim", "이미지 업로드 시작")
            ProfileImage.upload(Firebase.auth.currentUser!!.uid, uri)
        }
    }

    //나의 프로필 이미지 다운로드
    fun myProfileImageDownload() {
        myProfileImiageLoaded = true
        viewModelScope.launch {
            Log.w("Lim", "유저 프로필 이미지 불러오는 중")
            val uri = ProfileImage.getDownloadUrl(Firebase.auth.currentUser!!.uid)
            ProfileImage.updateUsersUriMap(Firebase.auth.currentUser!!.uid, uri)
            if(ProfileImage.usersUriMap[Firebase.auth.currentUser!!.uid] == null) {
                Log.w("Lim", "유저 프로필 이미지 불러오기 실패")
            }
            else{ Log.w("Lim", "유저 프로필 이미지 불러오기 성공") }
        }
    }

    //비밀번호 재설정
    fun mainFindPassword(navigation: NavController){
        Log.w("Lin", "SettingsScreen: 비밀번호 재설정")
        AuthViewModel.uiState = AuthUiState.FindPassword
        navigation.navigate(Routes.AUTH_SCREEN)
    }

    //로그아웃
    fun mainSignOut(navigation: NavController){
        Log.w("Lim", "SettingsScreen: 로그아웃")
        AuthViewModel.trySignOut()
        navigation.navigate(Routes.AUTH_SCREEN)
    }

    //회원탈퇴
    fun mainDeleteUser(navigation: NavController){
        Log.w("Lim", "SettingsScreen: 회원탈퇴 화면으로 이동")
        AuthViewModel.changeDeleteUserView()
        navigation.navigate(Routes.AUTH_SCREEN)
    }

    //정보수정
    fun editUserInfo(navigation: NavController){
        Log.w("Lim", "SettingsScreen: 정보 수정")
        AuthViewModel.userInputChecked = false
        AuthViewModel.uiState = AuthUiState.InputUserInfo
        AuthViewModel.inputStdNum = UserData.myInfo!!["std-num"]!!.toString()
        AuthViewModel.inputName = UserData.myInfo!!["name"]!!.toString()
        AuthViewModel.inputMbti = UserData.myInfo!!["mbti"]!!.toString()
        navigation.navigate(Routes.AUTH_SCREEN)
    }

    //유저 프로필 이미지 저장
    fun downlodUri(uid: String) {
        viewModelScope.launch {
            val uri = ProfileImage.getDownloadUrl(uid)
            ProfileImage.updateUsersUriMap(uid, uri)
        }
    }

    //유저 정보 저장
    fun downlodData(uid: String) {
        viewModelScope.launch {
            val data = UserData.get(uid)
            UserData.updateUsersDataMap(uid, data)
        }
    }

    //유저 차단 추가
    fun addUserIgnore(uid: String) {
        viewModelScope.launch {
            UserDataStore.userIgnoreListUpdate("ADD", uid)
            UserDataStore.setStringSetData("USER_IGNORE_LIST", UserDataStore.userIgnoreList)
            Log.w("addUserIgnore", "유저($uid) 차단")
        }
    }

    //유저 차단 제거
    fun removeUserIgnore(uid: String) {
        viewModelScope.launch {
            UserDataStore.userIgnoreListUpdate("REMOVE", uid)
            UserDataStore.setStringSetData("USER_IGNORE_LIST", UserDataStore.userIgnoreList)
            Log.w("removeUserIgnore", "유저($uid) 차단해제")
        }
    }

    fun makeDirectChatting(targetUid: String, mainNavigation: NavController) {
        viewModelScope.launch {
            val roomID = Chattings.makeDirectChatting(targetUid)
            Chattings.getRoomList()
            if(roomID != "") mainNavigation.navigate(Routes.CHATTING_SCREEN + "/${roomID}")
        }
    }
}
package hello.kwfriends.ui.screens.settings

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.preferenceDatastore.UserDataStore
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.ui.screens.auth.AuthUiState
import hello.kwfriends.ui.screens.auth.AuthViewModel
import hello.kwfriends.ui.main.Routes
import kotlinx.coroutines.launch

class SettingsViewModel: ViewModel() {


    //유저 설정 불러왔는지 여부
    var userSettingValuesLoaded by mutableStateOf<Boolean>(false)

    //다크모드 여부 저장
    var isDarkMode by mutableStateOf<Boolean?>(true)

    //조용모드 여부 저장
    var isQuietMode by mutableStateOf<Boolean?>(false)

    //유저 설정 세팅값들 불러오는 함수
    fun userSettingValuesLoad(){
        viewModelScope.launch {
            UserDataStore.getDataFlow().collect {
                isDarkMode = it[booleanPreferencesKey("SETTINGS_DARK_MODE")]
                isQuietMode = it[booleanPreferencesKey("SETTINGS_QUIET_MODE")]
                //유저 설정 기본값
                if(isDarkMode == null) isDarkMode = false
                if(isQuietMode == null) isQuietMode = false
            }
        }
    }

    //다크모드 스위치 변경 함수
    fun switchDarkMode(){
        viewModelScope.launch {
            UserDataStore.setBooleanData("SETTINGS_DARK_MODE", !isDarkMode!!)
        }
    }

    //조용모드 스위치 변경 함수
    fun switchQuietMode(){
        viewModelScope.launch {
            UserDataStore.setBooleanData("SETTINGS_QUIET_MODE", !isQuietMode!!)
        }
    }


    //자신의 프로필 이미지를 업로드함
    fun profileImageUpload(uri: Uri){
        viewModelScope.launch {
            Log.w("Lim", "이미지 업로드 시작")
            ProfileImage.upload(Firebase.auth.currentUser!!.uid, uri)
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
        AuthViewModel.inputStdNum = AuthViewModel.userInfo!!["std-num"]!!.toString()
        AuthViewModel.inputName = AuthViewModel.userInfo!!["name"]!!.toString()
        AuthViewModel.inputMbti = AuthViewModel.userInfo!!["mbti"]!!.toString()
        AuthViewModel.inputGender = AuthViewModel.userInfo!!["gender"]!!.toString()
        navigation.navigate(Routes.AUTH_SCREEN)
    }

    fun test(){
        val testMap = mapOf(
            "name" to "lim",
            "num" to 2023203045
        )
        viewModelScope.launch {
            UserData.update(testMap)
        }
    }
}
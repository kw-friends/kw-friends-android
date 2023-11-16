package hello.kwfriends.ui.screens.settings

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.datastoreManager.PreferenceDataStore
import hello.kwfriends.firebase.storageManager.ProfileImage
import hello.kwfriends.ui.screens.auth.AuthUiState
import hello.kwfriends.ui.screens.auth.AuthViewModel
import hello.kwfriends.ui.screens.main.Routes
import kotlinx.coroutines.launch

class SettingsViewModel: ViewModel() {

    //USER_DATA datastore 객체 저장 변수
    var preferencesDataStore by mutableStateOf<PreferenceDataStore?>(null)

    //유저 설정 불러왔는지 여부
    var userSettingValuesLoaded by mutableStateOf<Boolean>(false)

    //다크모드 여부 저장
    var isDarkMode by mutableStateOf<Boolean>(true)

    //조용모드 여부 저장
    var isQuietMode by mutableStateOf<Boolean>(false)

    //유저 설정 세팅값들 불러오는 함수
    fun userSettingValuesLoad(){
        viewModelScope.launch {
            preferencesDataStore!!.getData("IS_DARK_MODE").collect() {
                Log.w("Lim", "[LOAD] [IS_DARK_MODE]: $it")
                if(it == ""){
                    Log.w("Lim", "IS_DARK_MODE기본값으로 설정")
                    preferencesDataStore!!.setData("IS_DARK_MODE", "false")

                }
                isDarkMode = it.toBoolean()
            }
        }
        viewModelScope.launch {
            preferencesDataStore!!.getData("IS_QUIET_MODE").collect() {
                Log.w("Lim", "[LOAD] [IS_QUIET_MODE]: $it")
                if(it == ""){
                    Log.w("Lim", "IS_QUIET_MODE기본값으로 설정")
                    preferencesDataStore!!.setData("IS_QUIET_MODE", "false")
                }
                isQuietMode = it.toBoolean()
            }
        }
    }


    //다크모드 스위치 변경 함수
    fun switchDarkMode(){
        viewModelScope.launch {
            preferencesDataStore!!.setData("IS_DARK_MODE", (!isDarkMode).toString())
        }
    }

    //조용모드 스위치 변경 함수
    fun switchQuietMode(){
        viewModelScope.launch {
            preferencesDataStore!!.setData("IS_QUIET_MODE", (!isQuietMode).toString())
        }
    }


    //자신의 프로필 이미지를 업로드함
    fun profileImageUpload(uri: Uri){
        viewModelScope.launch {
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
        AuthViewModel.uiState = AuthUiState.DeleteUser
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
}
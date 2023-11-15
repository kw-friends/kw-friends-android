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
import hello.kwfriends.firebase.storageManager.ProfileImage
import hello.kwfriends.ui.screens.auth.AuthUiState
import hello.kwfriends.ui.screens.auth.AuthViewModel
import hello.kwfriends.ui.screens.main.Routes
import kotlinx.coroutines.launch

class SettingsViewModel: ViewModel() {

    //다크모드 여부 저장
    var isDarkMode by mutableStateOf<Boolean>(true)

    //조용모드 여부 저장
    var isQuietMode by mutableStateOf<Boolean>(false)


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
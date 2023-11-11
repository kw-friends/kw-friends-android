package hello.kwfriends.ui.screens.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import hello.kwfriends.firebase.firestoreManager.PostDetail
import hello.kwfriends.firebase.firestoreManager.PostManager
import hello.kwfriends.ui.screens.auth.AuthUiState
import hello.kwfriends.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var posts by mutableStateOf<List<PostDetail>>(listOf())

    //유저 검사 여부 저장
    var userFirstCheck by mutableStateOf<Boolean>(false)


    fun getPostFromFirestore() {
        Log.d("getPostFromFirestore()",  "데이터 가져옴")
        viewModelScope.launch {
            posts = PostManager.getPostRef()
        }
    }

    //로그아웃
    fun mainSignOut(goAuthNavigation: () -> Unit){
        Log.w("Lim", "SettingsScreen: 로그아웃")
        AuthViewModel.trySignOut()
        goAuthNavigation()
    }
    
    //회원탈퇴
    fun mainDeleteUser(navigation: NavController){
        Log.w("Lim", "SettingsScreen: 회원탈퇴 화면으로 이동")
        AuthViewModel.uiState = AuthUiState.DeleteUser
        navigation.navigate(Routes.AUTH_SCREEN)
    }
}
package hello.kwfriends.ui.splash

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.ui.screens.auth.AuthViewModel
import hello.kwfriends.ui.main.MainActivity

class SplashViewModel : ViewModel() {

    var processingState by mutableStateOf<String>("")

    suspend fun splashUserCheck(context: Context) {
        Log.w("Lim", "Splash 유저 검사 시작")
        val intent = Intent(context, MainActivity::class.java)
        var startPoint = "auth"
        processingState = SplashProcessingState.auth
        if(AuthViewModel.userAuthAvailableCheck()){
            Log.w("Lim", "유저 인증 유효성 검사 성공")
            processingState = SplashProcessingState.infoCheck
            if(AuthViewModel.userInfoCheck()) {
                Log.w("Lim", "정보 입력 검사 성공")
                startPoint = "home"
            }
            else{ Log.w("Lim", "정보 입력 검사 실패") }
        }
        else{ Log.w("Lim", "유저 인증 유효성 검사 실패") }

        if(startPoint == "auth") {
            Log.w("Lim", "인증 화면으로 이동")
        }
        else{
            Log.w("Lim", "유저 프로필 이미지 불러오는 중")
            processingState = SplashProcessingState.profileLoading
            ProfileImage.myImageUri = ProfileImage.getDownloadUrl(Firebase.auth.currentUser!!.uid)
            if(ProfileImage.myImageUri == null){ Log.w("Lim", "유저 프로필 이미지 불러오기 실패") }
            else{ Log.w("Lim", "유저 프로필 이미지 불러오기 성공") }

            Log.w("Lim", "홈 화면으로 이동")
        }

        processingState = SplashProcessingState.hello
        intent.putExtra("startPoint", startPoint)
        context.startActivity(intent)
    }

}
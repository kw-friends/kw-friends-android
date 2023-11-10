package hello.kwfriends.ui.splash

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import hello.kwfriends.ui.screens.auth.AuthViewModel
import hello.kwfriends.ui.screens.main.MainActivity

class SplashViewModel : ViewModel() {

    suspend fun SplashUserCheck(context: Context) {
        Log.w("Lim", "Splash 유저 검사 시작")
        val intent = Intent(context, MainActivity::class.java)
        var startPoint = "auth"
        if(AuthViewModel.userAuthAvailableCheck()){
            Log.w("Lim", "유저 인증 유효성 검사 성공")
            if(AuthViewModel.userInfoCheck()) {
                Log.w("Lim", "정보 입력 검사 성공")
                startPoint = "home"
                Log.w("Lim", "홈 화면으로 이동")
            }
            else{ Log.w("Lim", "정보 입력 검사 실패") }
        }
        else{ Log.w("Lim", "유저 인증 유효성 검사 실패") }

        if(startPoint == "auth") Log.w("Lim", "인증 화면으로 이동")
        intent.putExtra("startPoint", startPoint)
        context.startActivity(intent)
    }

}
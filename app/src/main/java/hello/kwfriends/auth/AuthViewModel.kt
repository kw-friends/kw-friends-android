package hello.kwfriends.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthViewModel: ViewModel(){

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Menu)

    var inputEmail by mutableStateOf<String?>("")
    var inputPassword by mutableStateOf<String?>("")

    fun setInputEmailText(text: String){
        inputEmail = text
    }
    fun setInputPasswordText(text: String){
        inputPassword = text
    }

    fun changeLoginView(){
        inputEmail = ""
        inputPassword = ""
        uiState = AuthUiState.SignIn
    }

    fun changeRegisterView(){
        inputEmail = ""
        inputPassword = ""
        uiState = AuthUiState.Register
    }

    fun tryRegister(){
        if(inputEmail == null || inputPassword == null){
            Log.w("Lim", "이메일 또는 비밀번호가 입력되지 않았습니다.")
        }
        else if(inputPassword?.length ?: 0 <= 6){
            Log.w("Lim", "비밀번호는 7자리 이상이여야 합니다.")
        }
        else {
            uiState = AuthUiState.Loading
            Firebase.auth.createUserWithEmailAndPassword(inputEmail ?: "", inputPassword ?: "")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) { //이메일 등록 성공
                        Log.w("Lim", "이메일 등록에 성공했습니다. 인증 메일을 전송합니다.")
                        tryEmailVerify()
                        uiState = AuthUiState.Menu
                    } else {
                        uiState = AuthUiState.Menu
                        Log.w("Lim", "이메일 등록에 실패했습니다.")
                    }
                }
        }
    }
    fun trySignIn(){
        Firebase.auth.signInWithEmailAndPassword(inputEmail ?: "", inputPassword ?: "")
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.w("Lim", "로그인 시도 성공")
                    if(Firebase.auth.currentUser?.isEmailVerified!!){
                        uiState = AuthUiState.SignInSuccess
                        Log.w("Lim", "로그인 성공!")
                    }
                    else{
                        uiState = AuthUiState.Menu
                        Log.w("Lim", "로그인 실패")
                    }
                }
                else{
                    uiState = AuthUiState.Menu
                    Log.w("Lim", "로그인 시도 실패")
                }
            }
    }
    fun logout(){
        Firebase.auth.signOut()
        Log.w("Lim", "로그아웃")
        uiState = AuthUiState.Menu
    }
    fun tryEmailVerify(){
        if(Firebase.auth?.currentUser?.email != null){
            Firebase.auth.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { sendTask ->
                    if(sendTask.isSuccessful){
                        uiState = AuthUiState.Menu
                        Log.w("Lim", "인증 메일 전송에 성공했습니다.")
                    }
                    else{
                        uiState = AuthUiState.Menu
                        Log.w("Lim", "인증 메일 전송에 실패했습니다.")
                    }
                }
        }
        else Log.w("Lim", "이메일이 등록되지 않아 인증메일을 전송할 수 없습니다.")
    }
    fun requestEmailVerify(){
        Log.w("Lim", "이메일 인증 요청")
        uiState = AuthUiState.RequestEmailVerify
    }
    fun confirmVerify(){
        uiState = AuthUiState.Loading
        FirebaseAuth.getInstance().currentUser!!.reload()
            .addOnCompleteListener { sendTask ->
                if(sendTask.isSuccessful){
                    if(Firebase.auth.currentUser?.isEmailVerified == true){
                        Firebase.auth.signOut()
                        changeLoginView()
                        Log.w("Lim", "이메일 인증 완료, 로그인하세요")
                    }
                    else{
                        uiState = AuthUiState.Menu
                        Log.w("Lim", "이메일 인증이 되지 않았습니다.")
                    }
                }
                else{
                    Log.w("Lim", "리로드 실패")
                }
            }
    }

    fun deleteUser(){
        uiState = AuthUiState.Loading
        Firebase.auth.currentUser?.delete()
        Log.w("Lim", "계정을 삭제했습니다.")
        logout()
    }

}
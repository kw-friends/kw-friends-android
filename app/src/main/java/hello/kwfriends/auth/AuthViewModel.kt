package hello.kwfriends.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
        if(inputEmail == null || inputPassword == null){ //이메일, 비밀번호 null 체크
            Log.w("Lim", "이메일 또는 비밀번호가 입력되지 않았습니다.")
            return
        }
        if (passwordSafetyCheck(inputPassword!!) == false) {
            Log.w("Lim", "비밀번호 안전성 검사 불통과")
            return
        }
        Log.w("Lim", "비밀번호 안전성 검사 통과")
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

    /*
        [ 비밀번호 규칙 참고자료 ]
        자바스크립트를 사용하여 비밀번호 기반 계정으로 Firebase에 인증하기 - https://firebase.google.com/docs/auth/web/password-auth?hl=ko
        IBM Security Identity Manager(비밀번호 보안 수준 규칙) - https://www.ibm.com/docs/ko/sim/7.0.1.13?topic=rules-password-strength
        한국보건산업진흥원 비밀번호 생성규칙 안내 - https://www.khidi.or.kr/includes/password.jsp
        한국인터넷진흥원 패스워드 선택 및 이용 안내서 - https://www.kisa.or.kr/2060305/form?postSeq=14&lang_type=KO#fnPostAttachDownload
        microsoft Create and use strong passwords - https://support.microsoft.com/en-us/windows/create-and-use-strong-passwords-c5cebb49-8c53-4f5e-2bc4-fe357ca048eb
        Password Strength Checker - https://www.security.org/how-secure-is-my-password/
         */
    fun passwordSafetyCheck(password: String): Boolean{
        if(inputPassword?.length ?: 0 < 8){ // 최대길이
            Log.w("Lim", "비밀번호는 8자리 이상이여야 합니다.")
            return false
        }
        else if(inputPassword?.length ?: 0 > 16){ // 최대길이
            Log.w("Lim", "비밀번호는 16자리 이하여야 합니다.")
            return false
        }
        else {
            val special_char_list =  listOf( 33, 34, 35, 36, 37, 38, 39, 42, 58, 59, 63, 64, 92, 94, 126 ) //사용 가능한 특수문자 리스트
            val include_special_char = mutableListOf<Char>() // 특수문자 카운트 변수
            var include_number = mutableListOf<Char>() // 숫자 카운트 변수
            var include_char = mutableListOf<Char>() // 문자 카운트 변수
            for(i in inputPassword!!){
                if(i.code in 48..57){ // 0~9까지의 숫자인지 확인
                    include_number.add(i)
                }
                else if(i.code in 65..90 || i.code in 97..122){ // 대문자 또는 소문자 영어인지 확인
                    include_char.add(i)
                }
                else if(i.code in special_char_list){ // 사용가능한 특수문자인지 확인
                    include_special_char.add(i)
                }
                else {
                    Log.w("Lim", "사용 불가능한 특수문자 ${i.toChar()}가 사용되었습니다.")
                    return false
                }
            }
            if(include_char.size == 0){
                Log.w("Lim", "문자가 포함되어야 합니다.")
                return false
            }
            else if(include_number.size == 0){
                Log.w("Lim", "숫자가 포함되어야 합니다.")
                return false
            }
            else if(include_special_char.size == 0){
                Log.w("Lim", "특수문자가 포함되어야 합니다.")
                return false
            }
        }
        return true
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

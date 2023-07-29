package hello.kwfriends.auth

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.FilterQuality.Companion.Low
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthViewModel: ViewModel(){

    //비밀번호로 입력 가능한 특수문자 목록
    private val special_char_list =  listOf( 33, 34, 35, 36, 37, 38, 39, 42, 58, 59, 63, 64, 92, 94, 126 ) //사용 가능한 특수문자 리스트

    //유저 화면 상태 저장 변수
    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Menu)

    var userInputChecked by mutableStateOf<Boolean?>(false) //firestore 유저 정보 확인 여부

    // -- TextField 입력 변수, 함수 --
    var inputEmail by mutableStateOf<String?>("")
    var inputPassword by mutableStateOf<String?>("")
    var inputPasswordConfirm by mutableStateOf<String?>("")
    var inputName by mutableStateOf<String?>("")
    var inputStdNum by mutableStateOf<String?>("")
    var inputMbti by mutableStateOf<String?>("")
    fun setInputEmailText(text: String) { inputEmail = text }
    fun setInputPasswordText(text: String) { inputPassword = text }
    fun setInputPasswordConfirmText(text: String) { inputPasswordConfirm = text }
    fun setInputNameText(text: String) { inputName = text }
    fun setInputStdNumText(text: String) { inputStdNum = text }
    fun setInputMbtiText(text: String) { inputMbti = text }

    // -- 뷰 변환 함수 --
    fun changeLoginView(){
        inputEmail = ""
        inputPassword = ""
        uiState = AuthUiState.SignIn
    }
    fun changeRegisterView(){
        inputEmail = ""
        inputPassword = ""
        inputPasswordConfirm = ""
        uiState = AuthUiState.Register
    }

    //회원가입 시도 함수
    fun tryRegister(){
        if(inputEmail == "" || inputPassword == ""){ //이메일, 비밀번호 입력 확인
            Log.w("Lim", "이메일 또는 비밀번호가 입력되지 않았습니다.")
            return
        }
        if(!emailRuleCheck(inputEmail!!)){
            Log.w("Lim", "이메일 형식 검사 불통과")
            return
        }
        if (!passwordSafetyCheck(inputPassword!!)) { // 안전성 검사
            Log.w("Lim", "비밀번호 안전성 검사 불통과")
            return
        }
        if(inputPassword != inputPasswordConfirm){ // 비밀번호 확인 일치 검사
            Log.w("Lim", "비밀번호 확인 불일치")
            return
        }
        Log.w("Lim", "비밀번호 확인 일치")
        Log.w("Lim", "이메일 등록 시도")
        uiState = AuthUiState.Loading
        Firebase.auth.createUserWithEmailAndPassword(inputEmail ?: "", inputPassword ?: "")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) { //이메일 등록 성공
                    Log.w("Lim", "이메일 등록에 성공했습니다. 인증 메일을 전송합니다.")
                        trySendAuthEmail()
                        uiState = AuthUiState.Menu
                    } else {
                        uiState = AuthUiState.Menu
                        Log.w("Lim", "이메일 등록에 실패했습니다.")
                    }
                }
    }

    //입력한 이메일 형식 확인 함수
    fun emailRuleCheck(email: String): Boolean{ //광운대학교 웹메일 주소 형식인지 확인
        val email_start_point = email.indexOf('@')
        if(email_start_point != -1 && email.slice(IntRange(email_start_point, email.length-1)).lowercase() == "@kw.ac.kr"){
            Log.w("Lim", "이메일 형식 검사 통과")
            return true
        }
        Log.w("Lim", "이메일이 광운대학교 웹메일 형식에 어긋납니다.")
        return false
    }
    /* [ 비밀번호 규칙 참고자료 ]
    자바스크립트를 사용하여 비밀번호 기반 계정으로 Firebase에 인증하기 - https://firebase.google.com/docs/auth/web/password-auth?hl=ko
    IBM Security Identity Manager(비밀번호 보안 수준 규칙) - https://www.ibm.com/docs/ko/sim/7.0.1.13?topic=rules-password-strength
    한국보건산업진흥원 비밀번호 생성규칙 안내 - https://www.khidi.or.kr/includes/password.jsp
    한국인터넷진흥원 패스워드 선택 및 이용 안내서 - https://www.kisa.or.kr/2060305/form?postSeq=14&lang_type=KO#fnPostAttachDownload
    microsoft Create and use strong passwords - https://support.microsoft.com/en-us/windows/create-and-use-strong-passwords-c5cebb49-8c53-4f5e-2bc4-fe357ca048eb
    Password Strength Checker - https://www.security.org/how-secure-is-my-password/ */
    //비밀번호 규칙 확인 함수
    fun passwordSafetyCheck(password: String): Boolean{
        if(inputPassword?.length ?: 0 < 8){ // 최대길이
            Log.w("Lim", "비밀번호는 8자리 이상이여야 합니다.")
            return false
        }
        if(inputPassword?.length ?: 0 > 16){ // 최대길이
            Log.w("Lim", "비밀번호는 16자리 이하여야 합니다.")
            return false
        }
        var include_special_char = mutableListOf<Char>() // 특수문자 카운트 변수
        var include_number = mutableListOf<Char>() // 숫자 카운트 변수
        var include_char = mutableListOf<Char>() // 문자 카운트 변수
        for(i in inputPassword!!){
            when (i.code) {
                in 48..57 -> { // 0~9까지의 숫자인지 확인
                    include_number.add(i)
                }
                in 65..90, in 97..122 -> { // 대문자 또는 소문자 영어인지 확인
                    include_char.add(i)
                }
                in special_char_list -> { // 사용가능한 특수문자인지 확인
                    include_special_char.add(i)
                }
                else -> {
                    Log.w("Lim", "사용 불가능한 특수문자 ${i.toChar()}가 사용되었습니다.")
                    return false
                }
            }
        }
        if(include_char.size == 0){
            Log.w("Lim", "문자가 포함되어야 합니다.")
            return false
        }
        if(include_number.size == 0){
            Log.w("Lim", "숫자가 포함되어야 합니다.")
            return false
        }
        if(include_special_char.size == 0){
            Log.w("Lim", "특수문자가 포함되어야 합니다.")
            return false
        }
        Log.w("Lim", "비밀번호 안전성 검사 통과")
        return true
    }

    //로그인 시도 함수
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

    //로그아웃 함수
    fun logout(){
        Firebase.auth.signOut()
        Log.w("Lim", "로그아웃")
        uiState = AuthUiState.Menu
    }

    //인증 이메일 전송 시도 함수
    fun trySendAuthEmail(){
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

    //이메일 인증 화면 이동 함수
    fun requestEmailVerify(){
        Log.w("Lim", "이메일 인증 요청")
        uiState = AuthUiState.RequestEmailVerify
    }

    //이메일 인증 확인 시도 함수
    fun tryEmailVerify(){
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
    
    //회원탈퇴 함수
    fun deleteUser(){
        uiState = AuthUiState.Loading
        Firebase.auth.currentUser?.delete()
        Log.w("Lim", "계정을 삭제했습니다.")
        logout()
    }
    
    //유저 정보 firestore에 저장 시도 함수
    fun trySaveUserInfo(){
        val user_info = hashMapOf(
            "name" to inputName,
            "MBTI" to inputMbti?.lowercase(),
            "std-num" to inputStdNum,
            "Verified date" to FieldValue.serverTimestamp()
        )
        if(!userInfoFormCheck(user_info)) { return }
        Firebase.firestore.collection("users").document(Firebase.auth.uid!!)
            .set(user_info)
            .addOnSuccessListener {
                Log.w(ContentValues.TAG, "유저 정보를 firestore에 성공적으로 저장했습니다.")
                uiState = AuthUiState.SignInSuccess
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "유저 정보를 firestore에 저장하는데 실패했습니다.", e) }
    }
    
    //유저가 입력한 유저 정보 형식 확인 함수
    fun userInfoFormCheck(user_info: HashMap<String, Any?>): Boolean {
        return true
    }
    
    //firestore에 저장되어있는 유저 정보 확인 함수
    fun userInfoInputedCheck(){
        uiState = AuthUiState.Loading
        Log.w("Lim", "firestore 유저 정보 정보 정상인지 확인중..")
        Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!).get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    Log.w(ContentValues.TAG, "data: ${document.data}")
                    //정보에 이상있으면 정보 입력창으로 이동
                    //userInfoFormCheck()
                    Log.w(ContentValues.TAG, "유저 정보 정상 체크 확인완료")
                    userInputChecked = true
                    uiState = AuthUiState.SignInSuccess
                } else {
                    Log.w(ContentValues.TAG, "유저 정보가 존재하지 않아 정보 입력창으로 이동합니다.")
                    uiState = AuthUiState.InputUserInfo
                }
            }
            .addOnFailureListener { e-> Log.w(ContentValues.TAG, "유저 정보를 불러오는데 실패했습니다.", e) }
    }
}
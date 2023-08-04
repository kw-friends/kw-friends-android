package hello.kwfriends.auth

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class AuthViewModel: ViewModel(){
    //비밀번호로 입력 가능한 특수문자 목록
    private val specialCharList =  listOf( 33, 34, 35, 36, 37, 38, 39, 42, 58, 59, 63, 64, 92, 94, 126 ) //사용 가능한 특수문자 리스트

    //학번: 2023(입학년도)/2(단과대번호)/03(학부번호)/045(학생번호)
    //학번에 들어가는 단과대 리스트
    private val collegeList = mapOf<Char, String>( '7' to "전자정보공과대학", '2' to "소프트웨어융합대학", '1' to "공과대학", '6' to "자연과학대학", '3' to "인문사회과학대학", '8' to "정책법학대학", '5' to "경영대학")
    //학번에 들어가는 학과 리스트
    private val departmentList = mapOf<String, Map<String, String>>(
        "전자정보공과대학" to mapOf( "27" to "건축학과", "17" to "건축공학과", "14" to "화학공학과", "16" to "환경공학과" ),
        "소프트웨어융합대학" to mapOf( "02" to "컴퓨터정보공학부", "03" to "소프트웨어학부", "04" to "정보융합학부" ),
        "공과대학" to mapOf( "27" to "건축학과", "17" to "건축공학과", "14" to "화학공학과", "16" to "환경공학과" ),
        "자연과학대학" to mapOf( "03" to "수학과", "10" to "전자바이오물리학과", "05" to "화학과", "13" to "스포츠융합학과", "12" to "정보콘텐츠학과" ),
        "인문사회과학대학" to mapOf( "04" to "국어국문학과", "22" to "영어산업학과", "23" to "미디어커뮤니케이션학부", "11" to "산업심리학과", "21" to "동북아문화산업학부" ),
        "정책법학대학" to mapOf( "02" to "행정학과", "04" to "국제학부", "03" to "법학부", "05" to "자산관리학과" ),
        "경영대학" to mapOf( "08" to "경영학부", "10" to "국제통상학부" ) )
    //학번에 들어가는 최소 입학년도
    private val minAdmissionYear = 1934
    //학번에 들어가는 최대 입학년도
    private var maxAdmissionYear = 2023
    //광운대학교 웹메일 및 오피스 웹메일 주소
    private val kwEmailType = listOf<String>("@kw.ac.kr", "@office.kw.ac.kr")

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

    //가능한 최대 입학년도 갱신(업데이트) 함수
    fun updateMaxStdNum(){
        val currentDate = SimpleDateFormat("yyyy-MM").format(System.currentTimeMillis())
        if(currentDate.slice(IntRange(5, 6)).toInt() >= 11){
            maxAdmissionYear = currentDate.slice(IntRange(0, 3)).toInt() + 1
        }
        else{
            maxAdmissionYear = currentDate.slice(IntRange(0, 3)).toInt()
        }
        Log.w("Lim", "최대 입학년도가 ${maxAdmissionYear}년으로 설정되었습니다.")
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
        if(email_start_point != -1 && email.slice(IntRange(email_start_point, email.length-1)).lowercase() in kwEmailType){
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
        var includeSpecialChar = mutableListOf<Char>() // 특수문자 카운트 변수
        var includeNumber = mutableListOf<Char>() // 숫자 카운트 변수
        var includeChar = mutableListOf<Char>() // 문자 카운트 변수
        for(i in inputPassword!!){
            when (i.code) {
                in 48..57 -> { // 0~9까지의 숫자인지 확인
                    includeNumber.add(i)
                }
                in 65..90, in 97..122 -> { // 대문자 또는 소문자 영어인지 확인
                    includeChar.add(i)
                }
                in specialCharList -> { // 사용가능한 특수문자인지 확인
                    includeSpecialChar.add(i)
                }
                else -> {
                    Log.w("Lim", "사용 불가능한 특수문자 ${i.toChar()}가 사용되었습니다.")
                    return false
                }
            }
        }
        if(includeChar.size == 0){
            Log.w("Lim", "문자가 포함되어야 합니다.")
            return false
        }
        if(includeNumber.size == 0){
            Log.w("Lim", "숫자가 포함되어야 합니다.")
            return false
        }
        if(includeSpecialChar.size == 0){
            Log.w("Lim", "특수문자가 포함되어야 합니다.")
            return false
        }
        Log.w("Lim", "비밀번호 안전성 검사 통과")
        return true
    }

    //로그인 시도 함수
    fun trySignIn(){
        uiState = AuthUiState.Loading
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
        uiState = AuthUiState.Loading
        Firebase.auth.signOut()
        Log.w("Lim", "로그아웃")
        uiState = AuthUiState.Menu
    }

    //인증 이메일 전송 시도 함수
    fun trySendAuthEmail(){
        uiState = AuthUiState.Loading
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
            ?.addOnSuccessListener {
                Log.w("Lim", "성공적으로 계정을 삭제했습니다.")
                userInputChecked = false
                logout()
            }
            ?.addOnFailureListener {
                Log.w("Lim", "계정을 삭제하는데 실패했습니다. error=${it}")
                uiState = AuthUiState.Menu
            }
    }
    
    //유저 정보 firestore에 저장 시도 함수
    fun trySaveUserInfo(){
        val user_info = mapOf(
            "name" to inputName,
            "mbti" to inputMbti?.lowercase(),
            "std-num" to inputStdNum,
            "verified date" to FieldValue.serverTimestamp()
        )
        if(!userInfoFormCheck(user_info)) { return }
        uiState = AuthUiState.Loading
        Firebase.firestore.collection("users").document(Firebase.auth.uid!!)
            .set(user_info)
            .addOnSuccessListener {
                Log.w(ContentValues.TAG, "유저 정보를 firestore에 성공적으로 저장했습니다.")
                uiState = AuthUiState.SignInSuccess
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "유저 정보를 firestore에 저장하는데 실패했습니다.", e)
                uiState = AuthUiState.Menu
            }

    }
    
    //유저가 입력한 유저 정보 형식 확인 함수
    //학번: 2023(입학년도)/2(단과대번호)/03(학부번호)/045(학생번호)
    fun userInfoFormCheck(user_info: Map<String, Any?>): Boolean {
        updateMaxStdNum() // 가능한 최대 입학년도 업데이트
        val name = user_info["name"].toString()
        val mbti = user_info["mbti"].toString()
        val stdNum = user_info["std-num"].toString()
        if(name == ""){
            Log.w("Lim", "유저 이름 입력 안됨.")
            return false
        }
        if(mbti == ""){
            Log.w("Lim", "mbti 입력 안됨.")
            return false
        }
        else if(
            !(mbti[0] == 'i' || mbti[0] == 'e') ||
            !(mbti[1] == 'n' || mbti[1] == 's') ||
            !(mbti[2] == 'f' || mbti[2] == 't') ||
            !(mbti[3] == 'p' || mbti[3] == 'j') ||
            (mbti.length != 4) ){
            Log.w("Lim", "mbti 형식 틀림.")
            return false
        }
        if(stdNum == ""){
            Log.w("Lim", "학번 입력 안됨.")
            return false
        }
        else if(stdNum.length != 10){
            Log.w("Lim", "학번이 10자리가 아님.")
            return false
        }
        else if(stdNum.slice(IntRange(0, 3)).toInt() !in minAdmissionYear..maxAdmissionYear){
            Log.w("Lim", "입학년도는 ${minAdmissionYear}부터 ${maxAdmissionYear}까지만 가능합니다.")
            return false
        }
        if(stdNum[4] !in collegeList){
            Log.w("Lim", "${stdNum[4]}는 확인되지 않은 단과대 번호입니다.")
        }
        else if(departmentList[collegeList[stdNum[4]]]?.contains(stdNum.slice(IntRange(5, 6))) != true){
            Log.w("Lim", "${stdNum.slice(IntRange(5, 6))}는 확인되지 않은 학과 번호입니다.")
        }
        return true
    }
    
    //firestore에 저장되어있는 유저 정보 확인 함수
    fun userInfoInputedCheck(){
        uiState = AuthUiState.Loading
        Log.w("Lim", "firestore 유저 정보 정보 정상인지 확인중..")
        Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    Log.w(ContentValues.TAG, "Firestore 유저 정보: ${document.data}")
                    if(!userInfoFormCheck(document.data!!)){
                        Log.w(ContentValues.TAG, "유저 정보 비정상. 정보 입력 화면으로 이동.")
                        uiState = AuthUiState.InputUserInfo
                    } else {
                        Log.w(ContentValues.TAG, "유저 정보 정상 체크 확인완료")
                        userInputChecked = true
                        uiState = AuthUiState.SignInSuccess
                    }
                } else {
                    Log.w(ContentValues.TAG, "유저 정보가 존재하지 않아 정보 입력창으로 이동합니다.")
                    uiState = AuthUiState.InputUserInfo
                }
            }
            .addOnFailureListener { e-> Log.w(ContentValues.TAG, "유저 정보를 불러오는데 실패했습니다.", e) }
    }
}
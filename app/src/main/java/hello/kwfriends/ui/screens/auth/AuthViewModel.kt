package hello.kwfriends.ui.screens.auth

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebaseManager.UserAuth
import hello.kwfriends.firestoreManager.UserDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class AuthViewModel : ViewModel() {
    //비밀번호로 입력 가능한 특수문자 목록
    private val specialCharList = listOf(33, 34, 35, 36, 37, 38, 39, 42, 58, 59, 63, 64, 92, 94, 126) //사용 가능한 특수문자 리스트

    //학번: 2023(입학년도)/2(단과대번호)/03(학부번호)/045(학생번호)
    //학번에 들어가는 단과대 리스트
    private val collegeList = mapOf<Char, String>('7' to "전자정보공과대학", '2' to "소프트웨어융합대학", '1' to "공과대학", '6' to "자연과학대학", '3' to "인문사회과학대학", '8' to "정책법학대학", '5' to "경영대학")

    //학번에 들어가는 학과 리스트
    private val departmentList = mapOf<String, Map<String, String>>(
        "전자정보공과대학" to mapOf("27" to "건축학과", "17" to "건축공학과", "14" to "화학공학과", "16" to "환경공학과"),
        "소프트웨어융합대학" to mapOf("02" to "컴퓨터정보공학부", "03" to "소프트웨어학부", "04" to "정보융합학부"),
        "공과대학" to mapOf("27" to "건축학과", "17" to "건축공학과", "14" to "화학공학과", "16" to "환경공학과"),
        "자연과학대학" to mapOf("03" to "수학과", "10" to "전자바이오물리학과", "05" to "화학과", "13" to "스포츠융합학과", "12" to "정보콘텐츠학과"),
        "인문사회과학대학" to mapOf("04" to "국어국문학과", "22" to "영어산업학과", "23" to "미디어커뮤니케이션학부", "11" to "산업심리학과", "21" to "동북아문화산업학부"),
        "정책법학대학" to mapOf("02" to "행정학과", "04" to "국제학부", "03" to "법학부", "05" to "자산관리학과"),
        "경영대학" to mapOf("08" to "경영학부", "10" to "국제통상학부")
    )

    //학번에 들어가는 최소 입학년도
    private val minAdmissionYear = 1934

    //학번에 들어가는 최대 입학년도
    private var maxAdmissionYear = 2023

    //유저 화면 상태 저장 변수
    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Menu)

    // -- 최초실행 --
    //firestore 유저 정보 확인 여부
    var userInputChecked by mutableStateOf<Boolean>(false)

    //유저 소속 자동 확인 함수 실행 여부
    var userDepartAuto by mutableStateOf<Boolean>(false)

    //아이디저장 체크 여부
    var idSaveChecked by mutableStateOf<Boolean>(false)

    //자동로그인 체크 여부
    var autoSignInChecked by mutableStateOf<Boolean>(false)

    // -- TextField 입력 변수, 함수 --
    var inputEmail by mutableStateOf<String>("")
    var inputPassword by mutableStateOf<String>("")
    var inputPasswordConfirm by mutableStateOf<String>("")
    var inputName by mutableStateOf<String>("")
    var inputStdNum by mutableStateOf<String>("")
    var inputMbti by mutableStateOf<String>("")
    var inputCollege by mutableStateOf<String>("")
    var inputDepartment by mutableStateOf<String>("")
    fun setInputEmailText(text: String) { inputEmail = text }
    fun setInputPasswordText(text: String) { inputPassword = text }
    fun setInputPasswordConfirmText(text: String) { inputPasswordConfirm = text }
    fun setInputNameText(text: String) { inputName = text }
    fun setInputStdNumText(text: String) { inputStdNum = text }
    fun setInputMbtiText(text: String) { inputMbti = text }
    fun setInputCollegeText(text: String) { inputCollege = text }
    fun setInputDepartmentText(text: String) { inputDepartment = text }

    // -- 뷰 변환 함수 --
    fun changeLoginView() {
        inputEmail = ""
        inputPassword = ""
        uiState = AuthUiState.SignIn
    }
    fun changeRegisterView() {
        inputEmail = ""
        inputPassword = ""
        inputPasswordConfirm = ""
        uiState = AuthUiState.Register
    }
    fun changeDeleteUserView() {
        inputEmail = ""
        inputPassword = ""
        uiState = AuthUiState.DeleteUser
    }
    fun changeFindPasswordView() {
        inputEmail = ""
        uiState = AuthUiState.FindPassword
    }

    //가능한 최대 입학년도 갱신(업데이트) 함수
    fun updateMaxStdNum() {
        val currentDate = SimpleDateFormat("yyyy-MM").format(System.currentTimeMillis())
        if (currentDate.slice(IntRange(5, 6)).toInt() >= 11) {
            maxAdmissionYear = currentDate.slice(IntRange(0, 3)).toInt() + 1
        } else {
            maxAdmissionYear = currentDate.slice(IntRange(0, 3)).toInt()
        }
        Log.w("Lim", "최대 입학년도가 ${maxAdmissionYear}년으로 설정되었습니다.")
    }

    //회원가입 함수
    suspend fun tryRegister() {
        inputEmail = autoEmailLink(inputEmail)
        if (inputEmail == "" || inputPassword == "") { //이메일, 비밀번호 입력 확인
            Log.w("Lim", "이메일 또는 비밀번호가 입력되지 않았습니다.")
            return
        }
        if (!emailRuleCheck(inputEmail)) {
            Log.w("Lim", "이메일 형식 검사 불통과")
            return
        }
        if (!passwordSafetyCheck(inputPassword)) { // 안전성 검사
            Log.w("Lim", "비밀번호 안전성 검사 불통과")
            return
        }
        if (inputPassword != inputPasswordConfirm) { // 비밀번호 확인 일치 검사
            Log.w("Lim", "비밀번호 확인 불일치")
            return
        }
        Log.w("Lim", "비밀번호 확인 일치")
        Log.w("Lim", "이메일 등록 시도")
        uiState = AuthUiState.Loading
        if(UserAuth.createUser(inputEmail, inputPassword)){
            trySendAuthEmail()
        }
        uiState = AuthUiState.Menu
    }

    //입력한 이메일 형식 확인 함수
    fun emailRuleCheck(email: String): Boolean { //광운대학교 웹메일 주소 형식인지 확인
        val email_start_point = email.indexOf('@')
        if (email_start_point != -1 && email.slice(IntRange(email_start_point, email.length - 1))
                .lowercase() == "@kw.ac.kr"
        ) {
            Log.w("Lim", "이메일 형식 검사 통과")
            return true
        }
        Log.w("Lim", "이메일이 광운대학교 웹메일 형식에 어긋납니다.")
        return false
    }

    //비밀번호 규칙 확인 함수
    fun passwordSafetyCheck(password: String): Boolean {
        if (password.length < 8) { // 최대길이
            Log.w("Lim", "비밀번호는 8자리 이상이여야 합니다.")
            return false
        }
        if (password.length > 16) { // 최대길이
            Log.w("Lim", "비밀번호는 16자리 이하여야 합니다.")
            return false
        }
        val includeSpecialChar = mutableListOf<Char>() // 특수문자 카운트 변수
        val includeNumber = mutableListOf<Char>() // 숫자 카운트 변수
        val includeChar = mutableListOf<Char>() // 문자 카운트 변수
        for (i in password) {
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
        if (includeChar.size == 0) {
            Log.w("Lim", "문자가 포함되어야 합니다.")
            return false
        }
        if (includeNumber.size == 0) {
            Log.w("Lim", "숫자가 포함되어야 합니다.")
            return false
        }
        if (includeSpecialChar.size == 0) {
            Log.w("Lim", "특수문자가 포함되어야 합니다.")
            return false
        }
        Log.w("Lim", "비밀번호 안전성 검사 통과")
        return true
    }

    //로그인 시도 함수
    suspend fun trySignIn() {
        uiState = AuthUiState.Loading
        inputEmail = autoEmailLink(inputEmail)
        if(UserAuth.signIn(inputEmail, inputPassword)){
            uiState = AuthUiState.SignInSuccess
        } else{
            uiState = AuthUiState.SignIn
        }
    }

    //이메일 @kw.ac.kr 자동으로 붙이기
    fun autoEmailLink(email: String): String {
        val result = if(email == "") {
            ""
        }
        else if (email.indexOf('@') == -1 && email.lowercase() !in "kw.ac.kr") { //@없음 && 실수로 @만 안 친 경우 아님:
            "$email@kw.ac.kr"
        } else if (email.indexOf('@') == email.length - 1) { // @뒤를 안 친 경우:
            "${email}kw.ac.kr"
        } else{
            email
        }
        return result
    }

    //로그아웃 함수
    fun trySignOut() {
        uiState = AuthUiState.Loading
        UserAuth.signOut()
        uiState = AuthUiState.Menu
    }

    //인증 이메일 전송 시도 함수
    suspend fun trySendAuthEmail() {
        uiState = AuthUiState.Loading
        if (Firebase.auth.currentUser?.email != null) {
            UserAuth.sendAuthEmail()
        } else Log.w("Lim", "이메일이 등록되지 않아 인증메일을 전송할 수 없습니다.")
        uiState = AuthUiState.Menu
    }

    //이메일 인증 화면 이동 함수
    fun requestEmailVerify() {
        Log.w("Lim", "이메일 인증 요청")
        uiState = AuthUiState.RequestEmailVerify
    }

    //이메일 인증 확인 시도 함수
    suspend fun tryEmailVerify() {
        uiState = AuthUiState.Loading
        if(UserAuth.reload()) {
            if (Firebase.auth.currentUser?.isEmailVerified == true) {
                UserAuth.signOut()
                changeLoginView()
                Log.w("Lim", "이메일 인증 완료, 로그인하세요")
            } else {
                uiState = AuthUiState.Menu
                Log.w("Lim", "이메일 인증이 되지 않았습니다.")
            }
        }
        else{ uiState = AuthUiState.Menu }
    }

    //회원탈퇴 함수
    suspend fun tryDeleteUser() {
        uiState = AuthUiState.Loading
        Log.w("Lim", "회원탈퇴시 재로그인 필요")
        val email = autoEmailLink(inputEmail)
        //재로그인
        if(!UserAuth.signIn(email, inputPassword)) {
            uiState = AuthUiState.Menu
            return
        }
        //유저 현재 상태 불러오기
        val lastState: String
        try {
            lastState = UserDataManager.getUserData()?.get("state").toString()
        } catch (e:Exception){
            uiState = AuthUiState.Menu
            return
        }
        //유저 상태 삭제됨으로 변경
        if(!UserDataManager.mergeSetUserData(mapOf("state" to "deleted"))) {
            uiState = AuthUiState.Menu
            return
        }
        Log.w("Lim", "유저 계정 상태 deleted로 변경됨.")
        //회원탈퇴
        if(UserAuth.deleteUser()){
            userInputChecked = false
            userDepartAuto = false
            UserAuth.signOut()
        }
        else{
            //유저 firestore 상태 롤백
            CoroutineScope(Dispatchers.IO).launch { UserDataManager.mergeSetUserData(mapOf("state" to lastState)) }
        }
        uiState = AuthUiState.Menu
    }

    //학번으로 유저 소속 인식
    suspend fun userDepartmentAutoRecognition() {
        userDepartAuto = true
        //유저 정보 불러오기
        val userInfo: Map<String, Any>?
        try {
            userInfo = UserDataManager.getUserData()
        }
        catch (e: Exception) {
            Log.w(ContentValues.TAG, "유저 정보를 불러오는데 실패했습니다.", e)
            return
        }

        if (userInfo != null) {
            Log.w(ContentValues.TAG, "Firestore 유저 정보: $userInfo")
            val tempStdNum = userInfo["std-num"].toString() //2023203045
            inputCollege = collegeList[tempStdNum[4]] ?: ""
            inputDepartment = departmentList[collegeList[tempStdNum[4]]]
                ?.get(tempStdNum.slice(IntRange(5, 6)).toString()) ?: ""
            Log.w("Lim", "소속 자동인식. 단과대:${inputCollege}, 학부:${inputDepartment}")
        } else {
            Log.w(ContentValues.TAG, "유저 정보가 존재하지 않음.")
        }
    }

    //유저 정보 firestore에 저장 시도 함수
    suspend fun trySaveUserInfo() {
        val userInfo = mapOf(
            "name" to inputName,
            "mbti" to inputMbti.lowercase(),
            "std-num" to inputStdNum,
        )
        if (!userInfoFormCheck(userInfo)) { return } //userInfo 형식 체크
        uiState = AuthUiState.Loading
        //유저 데이터 저장
        if(UserDataManager.mergeSetUserData(userInfo)) { uiState = AuthUiState.SignInSuccess }
        else { uiState = AuthUiState.Menu }

    }

    //유저 소속 정보 firesotre에 저장 시도 함수
    suspend fun trySaveUserDepartment() {
        val userInfo = mapOf(
            "college" to inputCollege,
            "department" to inputDepartment,
        )
        if (!userInfoDepartmentCheck(userInfo)) { return } //유저 소속 자동 인식, 형식 체크
        uiState = AuthUiState.Loading
        //유저 소속 정보 저장
        if(UserDataManager.mergeSetUserData(userInfo)) { uiState = AuthUiState.SignInSuccess }
        else {uiState = AuthUiState.Menu  }
    }

    //유저가 입력한 유저 정보 형식 확인 함수
    //학번: 2023(입학년도)/2(단과대번호)/03(학부번호)/045(학생번호)
    fun userInfoFormCheck(userInfo: Map<String, Any?>): Boolean {
        updateMaxStdNum() // 가능한 최대 입학년도 업데이트
        val name = userInfo["name"].toString()
        val mbti = userInfo["mbti"].toString()
        val stdNum = userInfo["std-num"].toString()
        if (name == "") {
            Log.w("Lim", "유저 이름 입력 안됨.")
            return false
        }
        if (mbti == "") {
            Log.w("Lim", "mbti 입력 안됨.")
            return false
        } else if (
            !(mbti[0] == 'i' || mbti[0] == 'e') ||
            !(mbti[1] == 'n' || mbti[1] == 's') ||
            !(mbti[2] == 'f' || mbti[2] == 't') ||
            !(mbti[3] == 'p' || mbti[3] == 'j') ||
            (mbti.length != 4)
        ) {
            Log.w("Lim", "mbti 형식 틀림.")
            return false
        }
        if (stdNum == "") {
            Log.w("Lim", "학번 입력 안됨.")
            return false
        } else if (stdNum.length != 10) {
            Log.w("Lim", "학번이 10자리가 아님.")
            return false
        } else if (stdNum.slice(IntRange(0, 3)).toInt() !in minAdmissionYear..maxAdmissionYear) {
            Log.w("Lim", "입학년도는 ${minAdmissionYear}부터 ${maxAdmissionYear}까지만 가능합니다.")
            return false
        }
        if (stdNum[4] !in collegeList) {
            Log.w("Lim", "${stdNum[4]}는 확인되지 않은 단과대 번호입니다.")
        } else if (departmentList[collegeList[stdNum[4]]]?.contains(
                stdNum.slice(IntRange(5, 6))) != true
        ) {
            Log.w("Lim", "${stdNum.slice(IntRange(5, 6))}는 확인되지 않은 학과 번호입니다.")
        }
        return true
    }

    //유저 소속(단과대, 학부) 체크
    fun userInfoDepartmentCheck(userInfo: Map<String, Any?>): Boolean {
        if (userInfo["college"].toString() == "" || !userInfo.containsKey("college")) {
            Log.w("Lim", "단과대 입력 안됨.")
            return false
        }
        if (userInfo["department"].toString() == "" || !userInfo.containsKey("department")) {
            Log.w("Lim", "학부 입력 안됨.")
            return false
        }
        return true
    }

    //firestore에 저장되어있는 유저 정보 확인 함수
    suspend fun userInfoCheck() {
        uiState = AuthUiState.Loading

        //유저 정보 불러오기
        val userInfo: Map<String, Any>?
        try { userInfo = UserDataManager.getUserData() }
        catch (e: Exception) {
            Log.w(ContentValues.TAG, "유저 정보를 불러오지 못했습니다. error=$e")
            uiState = AuthUiState.InputUserInfo
            return
        }

        if (userInfo != null) {
            if (userInfo["state"] != "available") { // 유저 상태 available 아니면 로그아웃
                UserAuth.signOut()
                Log.w(ContentValues.TAG, "유저 상태가 available이 아니라 로그아웃되었습니다.")

            } else if (!userInfoFormCheck(userInfo)) {
                Log.w(ContentValues.TAG, "유저 정보 비정상. 정보 입력 화면으로 이동.")
                uiState = AuthUiState.InputUserInfo
            } else if (!userInfoDepartmentCheck(userInfo)) {
                Log.w(ContentValues.TAG, "유저 소속 정보 비정상. 소속 정보 입력 화면으로 이동.")
                uiState = AuthUiState.InputUserDepartment
            } else {
                Log.w(ContentValues.TAG, "유저 정보 정상 체크 확인완료")
                userInputChecked = true
                uiState = AuthUiState.SignInSuccess
            }
        } else {
            Log.w(ContentValues.TAG, "첫 로그인입니다!")
            UserDataManager.mergeSetUserData(
                mapOf("state" to "available", "first-login" to FieldValue.serverTimestamp())
            )
            Log.w(ContentValues.TAG, "유저 정보가 존재하지 않아 정보 입력창으로 이동합니다.")
            uiState = AuthUiState.InputUserInfo
        }
    }

    //유저 인증 정보 유효성 확인 함수
    suspend fun userAuthAvailableCheck(){
        UserAuth.reload()
        if(Firebase.auth.currentUser == null || Firebase.auth.currentUser?.isEmailVerified != true){
            trySignOut()
            Log.w("Lim", "유저의 firebase 인증상태가 사용불가능하여 로그아웃되었습니다.")
        }
    }

    //비밀번호 재설정 이메일 전송 시도 함수
    suspend fun trySendPasswordResetEmail(){
        inputEmail = autoEmailLink(inputEmail)
        uiState = AuthUiState.Loading
        try {
            if(UserAuth.sendPasswordResetEmail(inputEmail)){
                uiState = AuthUiState.SignIn
            }
            else{
                uiState = AuthUiState.FindPassword
            }
        }
        catch (e: Exception){
            Log.w("Lim", "비밀번호 재설정 이메일 전송 시도 실패: ", e)
            uiState = AuthUiState.FindPassword
        }
    }

}
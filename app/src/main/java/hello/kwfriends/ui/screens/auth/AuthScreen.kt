package hello.kwfriends.ui.screens.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.R
import hello.kwfriends.firebase.datastoreManager.PreferenceDataStore
import hello.kwfriends.ui.component.ButtonStyle1
import hello.kwfriends.ui.component.CheckboxStyle1
import hello.kwfriends.ui.component.TextStyle1
import hello.kwfriends.ui.component.TextfieldStyle1
import hello.kwfriends.ui.component.TextfieldStyle2
import hello.kwfriends.ui.screens.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AuthScreen(modifier: Modifier = Modifier, viewModel: AuthViewModel) {
    val context = LocalContext.current

    //USER_DATA DataStore 객체 저장
    if(viewModel.preferencesDataStore == null){
        viewModel.preferencesDataStore = PreferenceDataStore(LocalContext.current, "USER_DATA")
    }

    if (viewModel.uiState == AuthUiState.SignIn) {
        if (Firebase.auth.currentUser != null) { // 로그인 된 상태일 때
            if (Firebase.auth.currentUser?.isEmailVerified == true) { //이메일 인증 검사
                Log.w("Lim", "로그인 기록 확인")
                viewModel.uiState = AuthUiState.SignInSuccess // 이메일 인증 완료된 계정
            } else {
                viewModel.uiState = AuthUiState.RequestEmailVerify // 이메일 인증 안된 계정
            }
        }
    }
    if (!viewModel.idSaveLoaded){
        viewModel.idSaveLoaded = true
        viewModel.userIdSaveCheckAndLoad() // 유저 아이디 저장 정보 불러오기
    }
    when (viewModel.uiState) {
        is AuthUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(size = 64.dp)
                        .align(Alignment.Center),
                    color = Color.Magenta,
                    strokeWidth = 6.dp
                )
            }

        }
        is AuthUiState.SignIn -> {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFE79898))
            )
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .requiredWidth(266.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(77.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "앱 로고",
                        modifier = Modifier
                            .size(102.dp)
                            .clickable {
                                context.startActivity(
                                    Intent(
                                        context,
                                        MainActivity::class.java
                                    )
                                )
                            }
                    )
                    Spacer(modifier = Modifier.height(80.dp))
                    TextfieldStyle1(
                        text = "KW WEB-MAIL",
                        icon = Icons.Default.Email,
                        value = viewModel.inputEmail,
                        onValueChange = { viewModel.setInputEmailText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        text = "PASSWORD",
                        icon = Icons.Default.Lock,
                        value = viewModel.inputPassword,
                        isPassword = true,
                        onValueChange = { viewModel.setInputPasswordText(it) }
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    Row(
                        modifier = Modifier.align(Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(7.dp))
                        CheckboxStyle1(
                            text = "아이디 저장",
                            checked = viewModel.idSaveChecked,
                            onCheckedChange = { viewModel.idSaveChecked = !viewModel.idSaveChecked },
                            onTextClicked = { viewModel.idSaveChecked = !viewModel.idSaveChecked }
                        )
                    }
                    Spacer(modifier = Modifier.height(33.dp))
                    ButtonStyle1(
                        text = "Login",
                        onClick = { viewModel.trySignIn() }
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()){
                        Row {
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "비밀번호를 잊으셨나요?",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight(300),
                                    color = Color(0xFFF1F1F1),
                                ),
                                modifier = Modifier.clickable { viewModel.changeFindPasswordView() }
                            )
                        }
                        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                            Text(
                                text = "회원가입",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight(300),
                                    color = Color(0xFFF1F1F1),
                                ),
                                modifier = Modifier.clickable { viewModel.changeRegisterView() }
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                        }
                    }
                }
            }

        }

        is AuthUiState.FindPassword -> {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFE79898))
            )
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .requiredWidth(266.dp)
                        .fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(77.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "앱 로고",
                        modifier = Modifier.size(102.dp)
                    )
                    Spacer(modifier = Modifier.height(54.dp))
                    TextStyle1("비밀번호 재설정")
                    Spacer(modifier = Modifier.height(38.dp))
                    TextfieldStyle2(
                        placeholder = "Web-Mail",
                        value = viewModel.inputEmail,
                        onValueChange = { viewModel.setInputEmailText(it) }
                    )
                    Spacer(modifier = Modifier.height(140.dp))
                    ButtonStyle1(text = "메일 전송하기", onClick = { viewModel.trySendPasswordResetEmail() })
                    Spacer(modifier = Modifier.height(11.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "이전화면",
                            color = Color(0xFFF1F1F1),
                            modifier = Modifier.clickable { viewModel.changeLoginView() }
                        )
                    }
                }
            }
        }

        is AuthUiState.Register -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFE79898))
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .requiredWidth(266.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(77.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "앱 로고",
                        modifier = Modifier.size(102.dp)
                    )
                    Spacer(modifier = Modifier.height(54.dp))
                    TextStyle1("회원 가입")
                    Spacer(modifier = Modifier.height(25.dp))
                    TextfieldStyle2(
                        placeholder = "KWANGWOON WEB-MAIL",
                        value = viewModel.inputEmail,
                        onValueChange = { viewModel.setInputEmailText(it) }
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    TextfieldStyle2(
                        placeholder = "Password",
                        isPassword = true,
                        value = viewModel.inputPassword,
                        onValueChange = { viewModel.setInputPasswordText(it) },
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    TextfieldStyle2(
                        placeholder = "Password Again",
                        isPassword = true,
                        value = viewModel.inputPasswordConfirm,
                        onValueChange = { viewModel.setInputPasswordConfirmText(it) }
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Spacer(modifier = Modifier.width(7.dp))
                        CheckboxStyle1(
                            text = "이용약관 및 개인정보 처리방침에 동의합니다.",
                            checked = true,
                            onCheckedChange = { },
                            onTextClicked = { }
                        )
                    }
                    Spacer(modifier = Modifier.height(33.dp))
                    ButtonStyle1(
                        text = "계정 만들기",
                        onClick = { viewModel.tryRegister() }
                    )
                    Spacer(modifier = Modifier.height(11.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "이전화면",
                            color = Color(0xFFF1F1F1),
                            modifier = Modifier.clickable { viewModel.changeLoginView() }
                        )
                    }
                }
            }
        }

        is AuthUiState.RequestEmailVerify -> {
            //Email verity request screen
            Column(
                modifier = modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                val intent =
                    remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://webmail.kw.ac.kr/")) }
                Spacer(modifier = Modifier.padding(10.dp))
                Text(text = "사용자 정보:")
                Text(text = "Email: ${Firebase.auth.currentUser?.email ?: "email 가져오지 못함"}")
                Text(text = "이메일 인증 후 [인증 완료] 버튼을 눌러주세요.")
                Spacer(modifier = Modifier.padding(10.dp))
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.tryEmailVerify() }) {
                    Text(text = "인증 완료")
                }
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { context.startActivity(intent) }) {
                    Text(text = "광운대학교 웹메일 확인하러 가기")
                }
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.trySendAuthEmail() }) {
                    Text(text = "이메일 인증 재요청하기")
                }
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.trySignOut() }) {
                    Text(text = "로그아웃하기")
                }
            }
        }

        is AuthUiState.InputUserInfo -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFE79898))
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .requiredWidth(266.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(77.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "앱 로고",
                        modifier = Modifier.size(102.dp)
                    )
                    Spacer(modifier = Modifier.height(54.dp))
                    TextStyle1("회원 정보 입력")
                    Spacer(modifier = Modifier.height(16.dp))
                    TextfieldStyle1(
                        text = "학번",
                        icon = Icons.Default.AccountBox,
                        value = viewModel.inputStdNum,
                        onValueChange = { viewModel.setInputStdNumText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        text = "이름",
                        icon = Icons.Default.Person,
                        value = viewModel.inputName,
                        onValueChange = { viewModel.setInputNameText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        text = "mbti",
                        icon = Icons.Default.Face,
                        value = viewModel.inputMbti,
                        onValueChange = { viewModel.setInputMbtiText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(7.dp))
                        CheckboxStyle1(
                            text = "남자",
                            checked = viewModel.inputGender == "male",
                            onCheckedChange = { viewModel.inputGender = "male" },
                            onTextClicked = { viewModel.inputGender = "male" }
                        )
                        Spacer(modifier = Modifier.width(17.dp))
                        CheckboxStyle1(
                            text = "여자",
                            checked = viewModel.inputGender == "female",
                            onCheckedChange = { viewModel.inputGender = "female" },
                            onTextClicked = { viewModel.inputGender = "female" }
                        )
                        Spacer(modifier = Modifier.width(17.dp))
                        CheckboxStyle1(
                            text = "기타",
                            viewModel.inputGender == "etc",
                            onCheckedChange = { viewModel.inputGender = "etc" },
                            onTextClicked = { viewModel.inputGender = "etc" }
                        )
                    }
                    Spacer(modifier = Modifier.height(38.dp))
                    ButtonStyle1(
                        text = "완료",
                        onClick = { viewModel.trySaveUserInfo() }
                    )
                }
            }
        }

        is AuthUiState.InputUserDepartment -> {
            if (!viewModel.userDepartAuto) { viewModel.userDepartmentAutoRecognition() } // 학번으로 유저 소속 자동인식

            Column(
                modifier = modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(5.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputCollege,
                    onValueChange = { viewModel.setInputCollegeText(it) },
                    singleLine = true,
                    placeholder = { Text("단과대") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputDepartment,
                    onValueChange = { viewModel.setInputDepartmentText(it) },
                    singleLine = true,
                    placeholder = { Text("학부") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.trySaveUserDepartment() }) {
                    Text(text = "입력 완료")
                }
            }
        }

        is AuthUiState.SignInSuccess -> {
            //유저 상태 정상인지 확인
            if (!viewModel.userInputChecked) {
                Log.w("Lim", "유저 정보 정상인지 확인중..")
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.userInfoCheck() //firestore 정보 검사
                    viewModel.userAuthAvailableCheck() //firebase 인증 검사
                }
            }

            //로그인 성공 후 화면
            Column(
                modifier = modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "사용자 정보:")
                Text(text = "Uid: ${Firebase.auth.currentUser?.uid ?: "uid 가져오지 못함"}")
                Text(text = "Email: ${Firebase.auth.currentUser?.email ?: "email 가져오지 못함"}")
                Text(text = "EmailVerified: ${Firebase.auth.currentUser?.isEmailVerified ?: "emailverified 가져오지 못함"}")
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.trySignOut() }) {
                    Text(text = "로그아웃하기")
                }
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.changeDeleteUserView() }) {
                    Text(text = "회원탈퇴하기")
                }
            }
        }

        is AuthUiState.DeleteUser -> {
            Column(
                modifier = modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(5.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputEmail,
                    onValueChange = { viewModel.setInputEmailText(it) },
                    singleLine = true,
                    placeholder = { Text(text = "광운대학교 웹메일") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputPassword,
                    onValueChange = { viewModel.setInputPasswordText(it) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    placeholder = { Text(text = "비밀번호") }
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.tryDeleteUser() }) {
                    Text(text = "회원탈퇴하기")
                }
                Spacer(modifier = Modifier.padding(2.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.uiState = AuthUiState.SignInSuccess }) {
                    Text(text = "이전화면으로")
                }
            }
        }
    }
}

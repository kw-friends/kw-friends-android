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
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.R
import hello.kwfriends.datastoreManager.PreferenceDataStore
import hello.kwfriends.ui.component.ButtonStyle1
import hello.kwfriends.ui.component.CheckboxStyle1
import hello.kwfriends.ui.component.TextStyle1
import hello.kwfriends.ui.component.TextfieldStyle1
import hello.kwfriends.ui.component.TextfieldStyle2
import hello.kwfriends.ui.screens.main.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AuthScreen(viewModel: AuthViewModel, navigation: NavController) {

    //USER_DATA DataStore 객체 저장
    if (viewModel.preferencesDataStore == null) {
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

    if (!viewModel.idSaveLoaded) {
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
                                navigation.navigate(Routes.HOME_SCREEN)
                            }
                    )
                    Spacer(modifier = Modifier.height(80.dp))
                    TextfieldStyle1(
                        placeholder = "KW WEB-MAIL",
                        icon = Icons.Default.Email,
                        value = viewModel.inputEmail,
                        onValueChange = { viewModel.setInputEmailText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        placeholder = "PASSWORD",
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
                            onCheckedChange = {
                                viewModel.idSaveChecked = !viewModel.idSaveChecked
                            },
                            onTextClicked = { viewModel.idSaveChecked = !viewModel.idSaveChecked }
                        )
                    }
                    Spacer(modifier = Modifier.height(33.dp))
                    ButtonStyle1(
                        text = "Login",
                        onClick = { viewModel.trySignIn() }
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
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
                    TextStyle1("비밀번호 재설정")
                    Spacer(modifier = Modifier.height(38.dp))
                    TextfieldStyle2(
                        placeholder = "Web-Mail",
                        value = viewModel.inputEmail,
                        onValueChange = { viewModel.setInputEmailText(it) }
                    )
                    Spacer(modifier = Modifier.height(140.dp))
                    ButtonStyle1(
                        text = "메일 전송하기",
                        onClick = { viewModel.trySendPasswordResetEmail() })
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
                    Spacer(modifier = Modifier.height(12.dp))
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
            val context = LocalContext.current
            val intent =
                remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://webmail.kw.ac.kr/")) }

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
                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.Text(
                        text = "인증 메일이 발송되었습니다!",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight(300),
                            color = Color(0xFFF1F1F1),
                        )
                    )
                    Spacer(modifier = Modifier.height(88.dp))
                    TextfieldStyle2(
                        value = Firebase.auth.currentUser?.email ?: "email 가져오지 못함",
                        onValueChange = {},
                        canValueChange = false,
                        placeholder = "KW WEB MAIL"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Spacer(modifier = Modifier.width(7.dp))
                            androidx.compose.material3.Text(
                                text = "웹메일 확인하러 가기",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight(300),
                                    color = Color(0xFFF1F1F1),
                                ),
                                modifier = Modifier.clickable { context.startActivity(intent) }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            androidx.compose.material3.Text(
                                text = "이메일 재전송",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight(300),
                                    color = Color(0xFFF1F1F1),
                                ),
                                modifier = Modifier.clickable { viewModel.trySendAuthEmail() }
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(94.dp))
                    ButtonStyle1(
                        text = "인증 완료",
                        onClick = { viewModel.tryEmailVerify() }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(7.dp))
                        androidx.compose.material3.Text(
                            text = "로그아웃",
                            color = Color(0xFFF1F1F1),
                            modifier = Modifier.clickable { viewModel.trySignOut() }
                        )
                    }
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
                        placeholder = "학번",
                        icon = Icons.Default.AccountBox,
                        value = viewModel.inputStdNum,
                        onValueChange = { viewModel.setInputStdNumText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        placeholder = "이름",
                        icon = Icons.Default.Person,
                        value = viewModel.inputName,
                        onValueChange = { viewModel.setInputNameText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        placeholder = "mbti",
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
            if (!viewModel.userDepartAuto) {
                viewModel.userDepartmentAutoRecognition()
            } // 학번으로 유저 소속 자동인식
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
                        placeholder = "단과대",
                        icon = Icons.Default.LocationOn,
                        value = viewModel.inputCollege,
                        onValueChange = { viewModel.setInputCollegeText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        placeholder = "학부",
                        icon = Icons.Default.Home,
                        value = viewModel.inputDepartment,
                        onValueChange = { viewModel.setInputDepartmentText(it) }
                    )
                    Spacer(modifier = Modifier.height(128.dp))

                    ButtonStyle1(
                        text = "완료",
                        onClick = { viewModel.trySaveUserDepartment() }
                    )
                }
            }
        }

        is AuthUiState.SignInSuccess -> {
            //유저 상태 정상인지 확인
            Log.w("Lim", "유저 정보 정상인지 확인중..")

            CoroutineScope(Dispatchers.Main).launch {
                if (!viewModel.userAuthChecked) {
                    Log.w("Lim", "유효성 검사 안되어있음. 진행")
                    if(viewModel.userAuthAvailableCheck()){
                        Log.w("Lim", "유효성 검사 성공")
                    }
                }
                if (!viewModel.userInputChecked) {
                    Log.w("Lim", "정보 입력 검사 안되어있음. 진행")
                    if(viewModel.userInfoCheck()) {
                        Log.w("Lim", "정보 입력 검사 성공")
                    }
                }
                if(viewModel.userAuthChecked && viewModel.userInputChecked) {
                    Log.w("Lim", "인증 갱신 및 정보 입력 확인 완료, 이후 화면으로 이동.")
                    navigation.navigate(Routes.HOME_SCREEN)
                }
            }

        }

        is AuthUiState.DeleteUser -> {
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
                    TextStyle1("회원 탈퇴")
                    Spacer(modifier = Modifier.height(16.dp))
                    TextfieldStyle1(
                        placeholder = "광운대학교 웹메일",
                        icon = Icons.Default.AccountBox,
                        value = viewModel.inputEmail,
                        onValueChange = { viewModel.setInputEmailText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        placeholder = "비밀번호",
                        icon = Icons.Default.Person,
                        value = viewModel.inputPassword,
                        onValueChange = { viewModel.setInputPasswordText(it) },
                        isPassword = true
                    )
                    Spacer(modifier = Modifier.height(128.dp))
                    ButtonStyle1(
                        text = "회원탈퇴",
                        onClick = { viewModel.tryDeleteUser() }
                    )
                    Spacer(modifier = Modifier.height(11.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "이전화면",
                            color = Color(0xFFF1F1F1),
                            modifier = Modifier.clickable {
                                viewModel.uiState = AuthUiState.SignInSuccess
                            }
                        )
                    }
                }
            }
        }
    }
}

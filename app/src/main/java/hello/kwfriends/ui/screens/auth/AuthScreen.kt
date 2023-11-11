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


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AuthScreen(navigation: NavController) {

    //USER_DATA DataStore 객체 저장
    if (AuthViewModel.preferencesDataStore == null) {
        AuthViewModel.preferencesDataStore = PreferenceDataStore(LocalContext.current, "USER_DATA")
    }

    if (AuthViewModel.uiState == AuthUiState.SignIn) {
        if (Firebase.auth.currentUser != null) { // 로그인 된 상태일 때
            if (Firebase.auth.currentUser?.isEmailVerified == true) { //이메일 인증 검사
                Log.w("Lim", "로그인 기록 확인")
                AuthViewModel.uiState = AuthUiState.SignInSuccess // 이메일 인증 완료된 계정
            } else {
                AuthViewModel.uiState = AuthUiState.RequestEmailVerify // 이메일 인증 안된 계정
            }
        }
    }

    if (!AuthViewModel.idSaveLoaded) {
        AuthViewModel.idSaveLoaded = true
        AuthViewModel.userIdSaveCheckAndLoad() // 유저 아이디 저장 정보 불러오기
    }
    when (AuthViewModel.uiState) {
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
                        value = AuthViewModel.inputEmail,
                        onValueChange = { AuthViewModel.setInputEmailText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        placeholder = "PASSWORD",
                        icon = Icons.Default.Lock,
                        value = AuthViewModel.inputPassword,
                        isPassword = true,
                        onValueChange = { AuthViewModel.setInputPasswordText(it) }
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    Row(
                        modifier = Modifier.align(Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(7.dp))
                        CheckboxStyle1(
                            text = "아이디 저장",
                            checked = AuthViewModel.idSaveChecked,
                            onCheckedChange = {
                                AuthViewModel.idSaveChecked = !AuthViewModel.idSaveChecked
                            },
                            onTextClicked = { AuthViewModel.idSaveChecked = !AuthViewModel.idSaveChecked }
                        )
                    }
                    Spacer(modifier = Modifier.height(33.dp))
                    ButtonStyle1(
                        text = "Login",
                        onClick = { AuthViewModel.trySignIn() }
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
                                modifier = Modifier.clickable { AuthViewModel.changeFindPasswordView() }
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
                                modifier = Modifier.clickable { AuthViewModel.changeRegisterView() }
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
                        value = AuthViewModel.inputEmail,
                        onValueChange = { AuthViewModel.setInputEmailText(it) }
                    )
                    Spacer(modifier = Modifier.height(140.dp))
                    ButtonStyle1(
                        text = "메일 전송하기",
                        onClick = { AuthViewModel.trySendPasswordResetEmail() })
                    Spacer(modifier = Modifier.height(11.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "이전화면",
                            color = Color(0xFFF1F1F1),
                            modifier = Modifier.clickable { AuthViewModel.changeLoginView() }
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
                        value = AuthViewModel.inputEmail,
                        onValueChange = { AuthViewModel.setInputEmailText(it) }
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    TextfieldStyle2(
                        placeholder = "Password",
                        isPassword = true,
                        value = AuthViewModel.inputPassword,
                        onValueChange = { AuthViewModel.setInputPasswordText(it) },
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    TextfieldStyle2(
                        placeholder = "Password Again",
                        isPassword = true,
                        value = AuthViewModel.inputPasswordConfirm,
                        onValueChange = { AuthViewModel.setInputPasswordConfirmText(it) }
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
                        onClick = { AuthViewModel.tryRegister() }
                    )
                    Spacer(modifier = Modifier.height(11.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "이전화면",
                            color = Color(0xFFF1F1F1),
                            modifier = Modifier.clickable { AuthViewModel.changeLoginView() }
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
                                modifier = Modifier.clickable { AuthViewModel.trySendAuthEmail() }
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(94.dp))
                    ButtonStyle1(
                        text = "인증 완료",
                        onClick = { AuthViewModel.tryEmailVerify() }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(7.dp))
                        androidx.compose.material3.Text(
                            text = "로그아웃",
                            color = Color(0xFFF1F1F1),
                            modifier = Modifier.clickable { AuthViewModel.trySignOut() }
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
                        value = AuthViewModel.inputStdNum,
                        onValueChange = { AuthViewModel.setInputStdNumText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        placeholder = "이름",
                        icon = Icons.Default.Person,
                        value = AuthViewModel.inputName,
                        onValueChange = { AuthViewModel.setInputNameText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        placeholder = "mbti",
                        icon = Icons.Default.Face,
                        value = AuthViewModel.inputMbti,
                        onValueChange = { AuthViewModel.setInputMbtiText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(7.dp))
                        CheckboxStyle1(
                            text = "남자",
                            checked = AuthViewModel.inputGender == "male",
                            onCheckedChange = { AuthViewModel.inputGender = "male" },
                            onTextClicked = { AuthViewModel.inputGender = "male" }
                        )
                        Spacer(modifier = Modifier.width(17.dp))
                        CheckboxStyle1(
                            text = "여자",
                            checked = AuthViewModel.inputGender == "female",
                            onCheckedChange = { AuthViewModel.inputGender = "female" },
                            onTextClicked = { AuthViewModel.inputGender = "female" }
                        )
                        Spacer(modifier = Modifier.width(17.dp))
                        CheckboxStyle1(
                            text = "기타",
                            AuthViewModel.inputGender == "etc",
                            onCheckedChange = { AuthViewModel.inputGender = "etc" },
                            onTextClicked = { AuthViewModel.inputGender = "etc" }
                        )
                    }
                    Spacer(modifier = Modifier.height(38.dp))
                    ButtonStyle1(
                        text = "완료",
                        onClick = { AuthViewModel.trySaveUserInfo() }
                    )
                }
            }
        }

        is AuthUiState.InputUserDepartment -> {
            if (!AuthViewModel.userDepartAuto) {
                AuthViewModel.userDepartmentAutoRecognition()
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
                        value = AuthViewModel.inputCollege,
                        onValueChange = { AuthViewModel.setInputCollegeText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        placeholder = "학부",
                        icon = Icons.Default.Home,
                        value = AuthViewModel.inputDepartment,
                        onValueChange = { AuthViewModel.setInputDepartmentText(it) }
                    )
                    Spacer(modifier = Modifier.height(128.dp))

                    ButtonStyle1(
                        text = "완료",
                        onClick = { AuthViewModel.trySaveUserDepartment() }
                    )
                }
            }
        }

        is AuthUiState.SignInSuccess -> {
            //유저 상태 정상인지 확인
            Log.w("Lim", "유저 정보 정상인지 확인중..")
            AuthViewModel.signInSuccessCheck(navigation)

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
                        value = AuthViewModel.inputEmail,
                        onValueChange = { AuthViewModel.setInputEmailText(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextfieldStyle1(
                        placeholder = "비밀번호",
                        icon = Icons.Default.Person,
                        value = AuthViewModel.inputPassword,
                        onValueChange = { AuthViewModel.setInputPasswordText(it) },
                        isPassword = true
                    )
                    Spacer(modifier = Modifier.height(128.dp))
                    ButtonStyle1(
                        text = "회원탈퇴",
                        onClick = { AuthViewModel.tryDeleteUser() }
                    )
                    Spacer(modifier = Modifier.height(11.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "돌아가기",
                            color = Color(0xFFF1F1F1),
                            modifier = Modifier.clickable {
                                //navigation.navigate(Routes.HOME_SCREEN)
                                //AuthViewModel.uiState = AuthUiState.SignInSuccess
                                AuthViewModel.signInSuccessCheck(navigation)
                            }
                        )
                    }
                }
            }
        }
    }
}

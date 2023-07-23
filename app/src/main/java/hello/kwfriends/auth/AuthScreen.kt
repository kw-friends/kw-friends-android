package hello.kwfriends.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun AuthScreen(modifier: Modifier = Modifier, viewModel: AuthViewModel){
    if(Firebase.auth?.currentUser != null){ // 로그인 이력 있으면 자동로그인
        if(Firebase.auth?.currentUser?.isEmailVerified == true){ //이메일 인증 검사
            viewModel.uiState = AuthUiState.SignInSuccess // 이메일 인증 완료된 계정
        }
        else{
            viewModel.uiState = AuthUiState.RequestEmailVerify // 이메일 인증 안된 계정
        }
    }
    when(viewModel.uiState){
        is AuthUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()){
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(size = 64.dp)
                        .align(Alignment.Center),
                    color = Color.Magenta,
                    strokeWidth = 6.dp
                )
            }

        }
        is AuthUiState.Menu -> {
            Column(modifier = modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(5.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.changeLoginView() }) {
                    Text(text = "로그인하기")
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.changeRegisterView() }) {
                    Text(text = "회원가입하기")
                }
            }
        }
        is AuthUiState.SignIn -> {
            Column(modifier = modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(5.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel?.inputEmail ?: "",
                    onValueChange = { viewModel.setInputEmailText(it) },
                    singleLine = true,
                    placeholder = { Text(text = "광운대학교 웹메일") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel?.inputPassword ?: "",
                    onValueChange = { viewModel.setInputPasswordText(it) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    placeholder = { Text(text = "비밀번호") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.trySignIn() }) {
                    Text(text = "로그인하기")
                }
                Spacer(modifier = Modifier.padding(2.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.uiState =
                    AuthUiState.Menu
                }) {
                    Text(text = "이전화면으로")
                }
            }
        }
        is AuthUiState.Register -> {
            Column(modifier = modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(5.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel?.inputEmail ?: "",
                    onValueChange = { viewModel.setInputEmailText(it) },
                    singleLine = true,
                    placeholder = { Text("광운대학교 웹메일") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel?.inputPassword ?: "",
                    onValueChange = { viewModel.setInputPasswordText(it) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    placeholder = { Text("비밀번호") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel?.inputPasswordConfirm ?: "",
                    onValueChange = { viewModel.setInputPasswordConfirmText(it) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    placeholder = { Text("비밀번호 확인") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.tryRegister() }) {
                    Text(text = "회원가입하기")
                }
                Spacer(modifier = Modifier.padding(2.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.uiState =
                    AuthUiState.Menu
                }) {
                    Text(text = "이전화면으로")
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
                val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://webmail.kw.ac.kr/")) }
                Spacer(modifier = Modifier.padding(10.dp))
                Text(text = "사용자 정보:")
                Text(text = "Email: ${Firebase.auth.currentUser?.email ?: "email 가져오지 못함"}")
                Text(text = "이메일 인증 후 [인증 완료] 버튼을 눌러주세요.")
                Spacer(modifier = Modifier.padding(10.dp))
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.confirmVerify() }) {
                    Text(text = "인증 완료")
                }
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { context.startActivity(intent) }) {
                    Text(text = "광운대학교 웹메일 확인하러 가기")
                }
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.tryEmailVerify() }) {
                    Text(text = "이메일 인증 재요청하기")
                }
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.logout() }) {
                    Text(text = "로그아웃하기")
                }
            }
        }
        is AuthUiState.SignInSuccess -> {
            //Call After Sign in Screen

            Column(
                modifier = modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "사용자 정보:")
                Text(text = "Uid: ${Firebase.auth.currentUser?.uid ?: "uid 가져오지 못함"}")
                Text(text = "Email: ${Firebase.auth.currentUser?.email ?: "email 가져오지 못함"}")
                Text(text = "EmailVerified: ${Firebase.auth.currentUser?.isEmailVerified ?: "emailverified 가져오지 못함"}")
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.logout() }) {
                    Text(text = "로그아웃하기")
                }
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.deleteUser() }) {
                    Text(text = "회원탈퇴하기")
                }
            }
        }
        is AuthUiState.InputUserInfo -> {
            Column(modifier = modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(5.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel?.inputStdNum ?: "",
                    onValueChange = { viewModel.setInputStdNumText(it) },
                    singleLine = true,
                    placeholder = { Text("학번") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel?.inputName ?: "",
                    onValueChange = { viewModel.setInputNameText(it) },
                    singleLine = true,
                    placeholder = { Text("이름") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel?.inputMbti ?: "",
                    onValueChange = { viewModel.setInputMbtiText(it) },
                    singleLine = true,
                    placeholder = { Text("MBTI") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.tryRegister() }) {
                    Text(text = "입력 완료")
                }
            }
        }
    }
}

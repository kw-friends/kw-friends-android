package hello.kwfriends.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.widget.CheckBox
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hello.kwfriends.R
import hello.kwfriends.auth.ui.theme.KWFriendsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(modifier: Modifier = Modifier, viewModel: AuthViewModel){
    if(viewModel.uiState == AuthUiState.Menu){
        if(Firebase.auth?.currentUser != null){ // 로그인 된 상태일 때
            if(Firebase.auth?.currentUser?.isEmailVerified == true){ //이메일 인증 검사
                Log.w("Lim", "로그인 기록 확인")
                viewModel.uiState = AuthUiState.SignInSuccess // 이메일 인증 완료된 계정
            }
            else{
                viewModel.uiState = AuthUiState.RequestEmailVerify // 이메일 인증 안된 계정
            }
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
                    value = viewModel.inputEmail ?: "",
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
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
                        Checkbox(checked = viewModel.idSaveChecked, onCheckedChange = { viewModel.idSaveChecked = !viewModel.idSaveChecked })
                        Text(text = "아이디 저장", modifier = Modifier.clickable { viewModel.idSaveChecked = !viewModel.idSaveChecked })
                        Checkbox(checked = viewModel.autoSignInChecked, onCheckedChange = { viewModel.autoSignInChecked = !viewModel.autoSignInChecked })
                        Text(text = "자동 로그인", modifier = Modifier.clickable { viewModel.autoSignInChecked = !viewModel.autoSignInChecked })
                    }
                }
                Spacer(modifier = Modifier.padding(5.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.trySignIn() }) {
                    Text(text = "로그인하기")
                }
                Spacer(modifier = Modifier.padding(2.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.uiState = AuthUiState.Menu }) {
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
                    onClick = { viewModel.signOut() }) {
                    Text(text = "로그아웃하기")
                }
            }
        }
        is AuthUiState.InputUserInfo -> {
            Column(modifier = modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(5.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputStdNum,
                    onValueChange = { viewModel.setInputStdNumText(it) },
                    singleLine = true,
                    placeholder = { Text("학번") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputName,
                    onValueChange = { viewModel.setInputNameText(it) },
                    singleLine = true,
                    placeholder = { Text("이름") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputMbti,
                    onValueChange = { viewModel.setInputMbtiText(it) },
                    singleLine = true,
                    placeholder = { Text("MBTI") }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.trySaveUserInfo() }) {
                    Text(text = "입력 완료")
                }
            }
        }
        is AuthUiState.InputUserDepartment -> {
            if(!viewModel.userDepartAuto){ viewModel.userDepartmentAutoRecognition() } // 학번으로 유저 소속 자동인식

            Column(modifier = modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.trySaveUserDepartment() }) {
                    Text(text = "입력 완료")
                }
            }
        }
        is AuthUiState.SignInSuccess -> {
            if(!viewModel.userInputChecked){ viewModel.userInfoInputedCheck() } //유저 정보 정상인지 확인

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
                    onClick = { viewModel.signOut() }) {
                    Text(text = "로그아웃하기")
                }
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.changeDeleteUserView() }) {
                    Text(text = "회원탈퇴하기")
                }
            }
        }
        is AuthUiState.DeleteUser -> {
            Column(modifier = modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(5.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputEmail ?: "",
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
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    CoroutineScope(Dispatchers.IO).launch{
                        viewModel.deleteUser()
                    }
                }) {
                    Text(text = "회원탈퇴하기")
                }
                Spacer(modifier = Modifier.padding(2.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.uiState = AuthUiState.SignInSuccess }) {
                    Text(text = "이전화면으로")
                }
            }
        }


    }
}

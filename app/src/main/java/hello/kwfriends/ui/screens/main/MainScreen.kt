package hello.kwfriends.ui.screens.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import hello.kwfriends.ui.screens.auth.AuthViewModel
import hello.kwfriends.ui.screens.findGathering.FindGatheringCardList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    navigation: NavController
) {

    CoroutineScope(Dispatchers.Main).launch {
        if (
            !mainViewModel.userFirstCheck &&
            (
                    Firebase.auth.currentUser == null //로그인 유무 검사
                            || Firebase.auth.currentUser?.isEmailVerified != true //이메일 인증 유무 검사
//                            || !authViewModel.userAuthChecked //인증 갱신 및 유효성 유무 검사
                            || !authViewModel.userInputChecked
                    )
        ) { // 로그인 된 상태일 때
            mainViewModel.userFirstCheck = true
            Log.w("Lim", "인증 화면으로 이동!")
            Log.w("Lim", "${authViewModel.userInputChecked}")
            navigation.navigate(Routes.AUTH_SCREEN)
        } else {
            Log.w("Lim", "${authViewModel.userInputChecked}")
        }
    }

    Log.w("Lim", "이후@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "KW Friends",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(5.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE2A39B)
                ),
                actions = {
                    IconButton(
                        onClick = { navigation.navigate(Routes.SETTINGS_SCREEN) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "모임 생성하기") },
                icon = { Icon(Icons.Default.Add, null) },
                onClick = {
                    navigation.navigate(Routes.NEW_POST_SCREEN)
                },
                modifier = Modifier.padding(bottom = 35.dp)
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            FindGatheringCardList(viewModel = mainViewModel)
        }
    }
}
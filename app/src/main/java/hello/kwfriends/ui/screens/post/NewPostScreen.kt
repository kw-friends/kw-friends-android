package hello.kwfriends.ui.screens.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hello.kwfriends.ui.component.TextfieldStyle3
import hello.kwfriends.ui.screens.main.MainViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(
    mainViewModel: MainViewModel,
    postViewModel: NewPostViewModel,
    navigation: NavController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackbarMessage by postViewModel.snackbarEvent.collectAsState()

    snackbarMessage?.let { message ->
        snackbarHostState.currentSnackbarData?.dismiss() // running snackbar 종료
        scope.launch {
            snackbarHostState.showSnackbar(message) // snackbar 표시
            postViewModel._snackbarEvent.value = null // _snackbarEvent 초기화
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "새 모임 생성",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE2A39B)
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navigation.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go to HomeScreen",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                // custom snackbar with the custom border
                Snackbar(
                    actionOnNewLine = true,
                    snackbarData = data
                )
            }
        }
    ) { paddingValues ->
        val context = LocalContext.current
        Column(modifier = Modifier.padding(paddingValues)) {
            Spacer(modifier = Modifier.size(10.dp))
            TextfieldStyle3(
                placeholder = "",
                value = postViewModel.gatheringTitle,
                onValueChange = { postViewModel.gatheringTitleChange(it) },
                imeAction = ImeAction.Next,
                isError = !postViewModel.gatheringTitleStatus,
                externalTitle = "모임 제목",
                errorMessage = "필수 항목",
            )
            TextfieldStyle3(
                placeholder = "",
                value = postViewModel.gatheringPromoter,
                canValueChange = false,
                onValueChange = {},
                externalTitle = "모임 주최자"
            )
            TextfieldStyle3(
                placeholder = "",
                value = postViewModel.gatheringLocation,
                onValueChange = { postViewModel.gatheringLocationChange(it) },
                isError = !postViewModel.gatheringLocationStatus,
                errorMessage = "필수 항목",
                imeAction = ImeAction.Next,
                externalTitle = "모임 위치",

                )
            TextfieldStyle3(
                placeholder = "",
                value = postViewModel.gatheringTime,
                onValueChange = { postViewModel.gatheringTimeChange(it) },
                isError = !postViewModel.gatheringTimeStatus,
                errorMessage = "필수 항목",
                imeAction = ImeAction.Next,
                externalTitle = "모임 시간"
            )
            TextfieldStyle3(
                placeholder = "",
                value = postViewModel.maximumMemberCount,
                onValueChange = { postViewModel.maximumMemberCountChange(it) },
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Decimal,
                isError = !postViewModel.maximumMemberCountStatus,
                errorMessage = "모임 인원은 최소 2명 이상의 정수여야 합니다.",
                externalTitle = "최대 인원 수"
            )
            TextfieldStyle3(
                value = postViewModel.gatheringDescription,
                onValueChange = { postViewModel.gatheringDescriptionChange(it) },
                isSingleLine = false,
                maxLines = 6,
                imeAction = ImeAction.Default,
                externalTitle = "모임 설명 (선택 사항)"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text(
                    text = "커뮤니티 가이드라인을 준수하여 모임을 생성해 주세요!",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xff888888)
                )

                Button(
                    onClick = {
                        if (!postViewModel.isUploading) {
                            if (!postViewModel.validateGatheringInfo()) {
                                postViewModel.showSnackbar("모임 정보가 부족합니다.")
                            } else {
                                postViewModel.uploadGatheringToFirestore()
                            }
                        }
                    },
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .align(Alignment.End)
                ) {
                    if (!postViewModel.isUploading) {
                        Text(text = "모임 만들기", style = MaterialTheme.typography.labelLarge)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color(0xFF833538),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.size(7.dp))
                            Text(text = "업로드 중..", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
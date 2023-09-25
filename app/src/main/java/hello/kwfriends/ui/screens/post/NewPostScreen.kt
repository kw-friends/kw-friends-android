package hello.kwfriends.ui.screens.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hello.kwfriends.ui.component.TextfieldStyle3
import hello.kwfriends.ui.screens.main.MainViewModel
import hello.kwfriends.ui.screens.main.ToolBarWithTitle


@Composable
fun NewPostScreen(mainViewModel: MainViewModel, postViewModel: NewPostViewModel) {
    Scaffold(
        topBar = { ToolBarWithTitle("새 모임 만들기") }
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
                value = postViewModel.gatheringDescription.toString(),
                onValueChange = { postViewModel.gatheringDescriptionChange(it) },
                isSingleLine = false,
                maxLines = 6,
                imeAction = ImeAction.Default,
                externalTitle = "모임 설명 (선택 사항)"
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(modifier = Modifier
                    .padding(15.dp)
                    .width(IntrinsicSize.Min)
                    .then(
                        Modifier
                            .width(IntrinsicSize.Min)
                            .widthIn(min = 110.dp)
                    ), onClick = { mainViewModel.goToHome() }) {
                    Text("뒤로가기")
                }
                Button(modifier = Modifier
                    .padding(15.dp),
                    onClick = { postViewModel.uploadGatheringToFirestore() }) {
                    if (!postViewModel.isUploading) {
                        Text("모임 만들기", fontSize = 14.sp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color(0xFF833538),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.size(7.dp))
                            Text(text = "업로드 중..", fontSize = 14.sp)
                        }

                    }
                }
            }
        }
    }
}
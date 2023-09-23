package hello.kwfriends.ui.screens.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import hello.kwfriends.ui.component.TextfieldStyle3
import hello.kwfriends.ui.screens.main.MainViewModel
import hello.kwfriends.ui.screens.main.ToolBarWithTitle

@Composable
fun NewPostScreen(mainViewModel: MainViewModel, postViewModel: NewPostViewModel) {
    Scaffold(
        topBar = { ToolBarWithTitle("새 모임 만들기") }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Spacer(modifier = Modifier.size(10.dp))
            TextfieldStyle3(
                placeholder = "모임 이름",
                value = postViewModel.gatheringTitle,
                onValueChange = { postViewModel.gatheringTitleChange(it) },
                imeAction = ImeAction.Next
            )
            TextfieldStyle3(
                placeholder = "모임 주최자",
                value = postViewModel.gatheringPromoter,
                canValueChange = false,
                onValueChange = {})
            TextfieldStyle3(
                placeholder = "모임 위치",
                value = postViewModel.gatheringLocation,
                onValueChange = { postViewModel.gatheringLocationChange(it) },
                imeAction = ImeAction.Next
            )
            TextfieldStyle3(
                placeholder = "모임 시기",
                value = postViewModel.gatheringTime,
                onValueChange = { postViewModel.gatheringTimeChange(it) },
                imeAction = ImeAction.Next
            )
            TextfieldStyle3(
                placeholder = "최대 인원 수",
                value = postViewModel.maximumMemberCount.toString(),
                onValueChange = {},
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Decimal,
                externalTitle = "최대 인원 수"
            )
            Spacer(modifier = Modifier.size(5.dp))
            TextfieldStyle3(
                value = postViewModel.gatheringDescription.toString(),
                onValueChange = { postViewModel.gatheringDescriptionChange(it) },
                isSingleLine = false,
                maxLines = 6,
                imeAction = ImeAction.Default,
                externalTitle = "모임 설명"
            )

            Button(modifier = Modifier.padding(15.dp), onClick = { mainViewModel.goToHome() }) {
                Text("뒤로가기")
            }
        }
    }
}
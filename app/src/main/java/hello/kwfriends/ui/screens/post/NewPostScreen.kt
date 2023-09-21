package hello.kwfriends.ui.screens.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hello.kwfriends.ui.component.TextfieldStyle2
import hello.kwfriends.ui.screens.main.MainViewModel
import hello.kwfriends.ui.screens.main.ToolBarWithTitle

@Composable
fun NewPostScreen(mainViewModel: MainViewModel, postViewModel: NewPostViewModel) {
    Scaffold(
        topBar = { ToolBarWithTitle("새 모임 만들기") }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TextfieldStyle2(
                placeholder = "모임 이름",
                value = postViewModel.gatheringTitle,
                onValueChange = {postViewModel.gatheringTitleChange(it)})
            TextfieldStyle2(
                placeholder = "모임 주최자",
                value = postViewModel.gatheringPromoter,
                canValueChange = false,
                onValueChange = {})
            TextfieldStyle2(
                placeholder = "모임 위치",
                value = postViewModel.gatheringLocation,
                onValueChange = {postViewModel.gatheringLocationChange(it)})
            TextfieldStyle2(
                placeholder = "모임 시기",
                value = postViewModel.gatheringTime,
                onValueChange = {postViewModel.gatheringTimeChange(it)})
            TextfieldStyle2(
                placeholder = "최대 인원 수",
                value = postViewModel.maximumMemberCount.toString(),
                onValueChange = {postViewModel.maximumMemberCountChange(it)})

            Button(onClick = { mainViewModel.goToHome() }) {
                Text("뒤로가기")
            }
        }
    }
}
package hello.kwfriends.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hello.kwfriends.ui.component.BottomNavigationBar
import hello.kwfriends.ui.screens.home.GatheringCard
import hello.kwfriends.ui.screens.myPage.UserInfoCard
import hello.kwfriends.ui.screens.settings.SettingsScreen

@Composable
fun ToolBarWithTitle(title: String, modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(text = title, fontSize = 22.sp) },
        backgroundColor = Color(0xFFE4C5C5),
        modifier = modifier
    )
}

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    when (viewModel.uiState) {
        MainUiState.Home -> {
            Column {
                ToolBarWithTitle(title = "모임 찾기")
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    GatheringCard()
                    BottomNavigationBar(viewModel = viewModel)
                }
            }
        }

        MainUiState.Settings -> {
            Column {
                ToolBarWithTitle(title = "설정", modifier = Modifier.padding(bottom = 30.dp))
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    SettingsScreen()
                    BottomNavigationBar(viewModel = viewModel)
                }
            }
        }

        MainUiState.MyPage -> {
            Column {
                ToolBarWithTitle(title = "마이페이지")
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    UserInfoCard(userName = "어승경", admissionyear = 23, major = "소프트웨어학부", grade = 1)
                    BottomNavigationBar(viewModel = viewModel)
                }
            }
        }
    }
}
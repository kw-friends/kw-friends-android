package hello.kwfriends.screens

import android.util.Log
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
import hello.kwfriends.screens.home.GatheringCard
import hello.kwfriends.screens.settings.SettingsScreen

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
                    Text(text = "마ㅡ이페이지마ㅡ이페이지마ㅡ이페이지마ㅡ이페이지", modifier = Modifier.padding(40.dp))
                    BottomNavigationBar(viewModel = viewModel)
                }
            }
        }
    }
}
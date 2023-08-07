package hello.kwfriends.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hello.kwfriends.screens.home.GatheringCard

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    when (viewModel.uiState) {
        MainUiState.Home -> {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
                Log.d("minmul", "Home")
                GatheringCard()
                BottomNavigationBar(viewModel = viewModel)
            }
        }

        MainUiState.Settings -> {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
                Log.d("minmul", "Settings")
                Text(text = "설정설정설정", modifier = Modifier.padding(40.dp))
                BottomNavigationBar(viewModel = viewModel)
            }
        }

        MainUiState.MyPage -> {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
                Log.d("minmul", "Mypage")
                Text(text = "마ㅡ이페이지마ㅡ이페이지마ㅡ이페이지마ㅡ이페이지", modifier = Modifier.padding(40.dp))
                BottomNavigationBar(viewModel = viewModel)
            }
        }
    }
}
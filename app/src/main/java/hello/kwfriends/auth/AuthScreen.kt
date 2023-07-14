package hello.kwfriends.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

@Composable
fun AuthScreen(modifier: Modifier = Modifier, viewModel: AuthViewModel){
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
            Column(modifier = modifier) {
                Text(text = "메뉴화면")
            }
        }
        else -> {

        }
    }
}
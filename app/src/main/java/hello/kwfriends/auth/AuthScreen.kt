package hello.kwfriends.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel

@Composable
fun AuthScreen(modifier: Modifier = Modifier, viewModel: AuthViewModel){
    when(viewModel.uiState){
        is AuthUiState.Menu -> {
            Column(modifier = modifier) {
                Text(text = "메뉴화면")
            }
        }
    }
}
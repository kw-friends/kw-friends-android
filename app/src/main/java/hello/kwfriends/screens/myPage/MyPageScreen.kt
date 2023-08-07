package hello.kwfriends.screens.myPage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview(showSystemUi = true)
@Composable
fun MyPageScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "My Page Screen")
    }
}
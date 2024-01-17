package hello.kwfriends.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun notChatScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "대충 채팅방", style = MaterialTheme.typography.titleLarge)
    }
}
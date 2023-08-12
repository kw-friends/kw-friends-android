package hello.kwfriends.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import hello.kwfriends.ui.theme.KWFriendsTheme

class AuthMainActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KWFriendsTheme {
                AuthScreen(viewModel = viewModel);
            }
        }
    }
}
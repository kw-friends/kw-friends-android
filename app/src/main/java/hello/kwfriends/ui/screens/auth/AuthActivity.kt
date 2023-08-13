package hello.kwfriends.ui.screens.auth

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hello.kwfriends.base.BaseActivity
import hello.kwfriends.ui.theme.KWFriendsTheme

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

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
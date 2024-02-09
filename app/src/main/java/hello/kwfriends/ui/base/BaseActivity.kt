package hello.kwfriends.ui.base

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import hello.kwfriends.ui.theme.KWFriendsTheme

abstract class BaseActivity : ComponentActivity() {

    protected fun setScreen(screen: @Composable () -> Unit) =
        setContent {
            val isDarkMode = isSystemInDarkTheme()
            SideEffect {

            }

            KWFriendsTheme(content = screen)
        }

}
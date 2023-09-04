package hello.kwfriends.ui.base

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import hello.kwfriends.ui.theme.KWFriendsTheme
import hello.kwfriends.ui.theme.KwFriendsDarkColors
import hello.kwfriends.ui.theme.KwFriendsLightColors

abstract class BaseActivity : ComponentActivity() {

    protected fun setScreen(screen: @Composable () -> Unit) =
        setContent {
            val systemUiController: SystemUiController = rememberSystemUiController()
            val isDarkMode = isSystemInDarkTheme()
            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = if (isDarkMode) {
                        KwFriendsDarkColors.background
                    } else {
                        KwFriendsLightColors.background
                    },
                    darkIcons = !isDarkMode
                )
            }

            KWFriendsTheme(content = screen)
        }

}
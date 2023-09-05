package hello.kwfriends.ui.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import hello.kwfriends.ui.base.BaseActivity
import hello.kwfriends.ui.theme.KWFriendsTheme

class MainActivity : BaseActivity() {
    private val viewModel: MainViewModel by viewModels();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setScreen {
            MainScreenView()
        }
    }

}
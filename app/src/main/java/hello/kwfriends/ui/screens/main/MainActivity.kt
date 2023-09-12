package hello.kwfriends.ui.screens.main

import android.os.Bundle
import androidx.activity.viewModels
import hello.kwfriends.ui.base.BaseActivity

class MainActivity : BaseActivity() {
    private val viewModel: MainViewModel by viewModels();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setScreen {
            MainScreen()
        }
    }

}
package hello.kwfriends.ui.screens.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import hello.kwfriends.ui.base.BaseActivity


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private val viewModel: SplashViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen(viewModel = viewModel)
        }


    }
}
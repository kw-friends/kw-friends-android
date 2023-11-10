package hello.kwfriends.ui.splash

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import hello.kwfriends.ui.screens.main.MainActivity
import kotlinx.coroutines.delay

class SplashViewModel : ViewModel() {

    suspend fun test(context: Context) {
        Log.w("Lim", "test")
        val intent = Intent(context, MainActivity::class.java)
        delay(2000)

        intent.putExtra("startPoint", "home")
        context.startActivity(intent)
    }

}
package hello.kwfriends.ui.splash

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hello.kwfriends.R
import hello.kwfriends.preferenceDatastore.UserDataStore
import hello.kwfriends.ui.theme.md_theme_light_primaryContainer

@Composable
fun SplashScreen(viewModel: SplashViewModel) {
    val context = LocalContext.current
    val alpha = remember {
        Animatable(0f)
    }
    //UserDataStore 객체 생성
    try { UserDataStore.pref }
    catch(e: Exception) { UserDataStore(context = LocalContext.current) }

    LaunchedEffect(key1 = true, block = {
        Log.w("Lim", "Splash 시작")
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000)
        )
        viewModel.splashUserCheck(context)
    })
    Column(
        Modifier
            .background(color = Color(0xFFE79898))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "앱 로고",
            modifier = Modifier
                .size(102.dp)
                .alpha(alpha.value)
                .weight(5f)
        )
        Text(
            modifier = Modifier.weight(1f),
            text = if(viewModel.processingState != "") viewModel.processingState else "라면 끓이는 중",
            color = md_theme_light_primaryContainer,
            style = MaterialTheme.typography.labelLarge
        )
    }

}


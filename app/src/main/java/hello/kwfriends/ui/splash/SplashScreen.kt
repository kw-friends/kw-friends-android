package hello.kwfriends.ui.splash

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hello.kwfriends.R
import hello.kwfriends.ui.screens.main.MainActivity
import hello.kwfriends.ui.theme.KWFriendsTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true, block = {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500)
        )
        delay(2000)
        context.startActivity(
            Intent(
                context,
                MainActivity::class.java
            )
        )
    })
    Box(
        Modifier
            .background(color = Color(0xFFE79898))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "앱 로고",
            modifier = Modifier
                .size(102.dp)
                .alpha(alpha.value)
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    KWFriendsTheme {
        SplashScreen()
    }
}
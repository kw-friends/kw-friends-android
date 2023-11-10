package hello.kwfriends.ui.splash

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
import androidx.compose.ui.unit.dp
import hello.kwfriends.R

@Composable
fun SplashScreen(viewModel: SplashViewModel) {
    val context = LocalContext.current
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true, block = {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500)
        )
        viewModel.SplashUserCheck(context)
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


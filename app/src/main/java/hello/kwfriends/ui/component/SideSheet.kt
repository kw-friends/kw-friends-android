package hello.kwfriends.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SideSheet(
    isShow: Boolean,
    onDissmiss: () -> Unit,
    content: @Composable () -> Unit
) {
    //Side Sheet 외부 배경 어둡게 변환
    val backgroundColor by animateColorAsState(
        targetValue = if (isShow) Color.Black.copy(alpha = 0.7f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300), label = "" // 1초 동안 색상 전환
    )
    val interactionSource = remember { MutableInteractionSource() }
    // 오른쪽에서 슬라이드하여 나타나는 Side Sheet
    AnimatedVisibility(
        visible = isShow,
        enter = slideInHorizontally(
            // 오른쪽에서 시작
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutHorizontally(
            // 오른쪽으로 사라짐
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = 300)
        ),
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        BackHandler {
            onDissmiss()
        }
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onDissmiss() }
                    )
                    .fillMaxHeight()
                    .weight(1f)
            )
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {}
                    )
                    .fillMaxHeight()
                    .width(290.dp) //side sheet 너비
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                content()
            }
        }
    }
}
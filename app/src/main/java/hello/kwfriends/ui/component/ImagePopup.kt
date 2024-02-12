package hello.kwfriends.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import hello.kwfriends.R
import hello.kwfriends.firebase.storage.ChattingImage

@Composable
fun ImagePopup(
    isShow: Boolean,
    onDismiss: () -> Unit, 
    imageUri: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    if (isShow) {
        Popup(
            onDismissRequest = { onDismiss() }
        ) {
            BackHandler {
                onDismiss()
            }
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onDismiss()
                    }
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                AsyncImage(
                    model = ChattingImage.chattingUriMap[imageUri],
                    placeholder = painterResource(id = R.drawable.test_image),
                    contentDescription = "chatting room image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(30.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}
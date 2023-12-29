package hello.kwfriends.ui.screens.post.newPost

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun NewPostPopup(
    state: Boolean,
    onDismiss: () -> Unit,
    newPostViewModel: NewPostViewModel,
) {
    if (state) {
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true)
        ) {
            BackHandler {
                onDismiss()
            }
            NewPostScreen(
                newPostViewModel = newPostViewModel,
                onDismiss = onDismiss
            )
        }
    }
}
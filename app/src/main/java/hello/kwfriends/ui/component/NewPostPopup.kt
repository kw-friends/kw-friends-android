package hello.kwfriends.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import hello.kwfriends.ui.screens.newPost.NewPostScreen
import hello.kwfriends.ui.screens.newPost.NewPostViewModel

@Composable
fun NewPostPopup(
    state: Boolean,
    onDismiss: () -> Unit,
    newPostViewModel: NewPostViewModel
) {
    if(state) {
        Popup(
            onDismissRequest = onDismiss,
        ) {
            NewPostScreen(
                newPostViewModel = newPostViewModel,
                onDismiss = onDismiss
            )

        }
    }
}
package hello.kwfriends.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import hello.kwfriends.ui.screens.newPost.NewPostScreen
import hello.kwfriends.ui.screens.newPost.NewPostViewModel

@Composable
fun NewPostDialog(
    state: Boolean,
    onDismiss: () -> Unit,
    newPostViewModel: NewPostViewModel
) {
    if(state) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            NewPostScreen(
                newPostViewModel = newPostViewModel,
                onDismiss = onDismiss
            )

        }
    }
}
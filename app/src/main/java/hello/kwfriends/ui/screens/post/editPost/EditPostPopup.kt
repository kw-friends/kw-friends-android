package hello.kwfriends.ui.screens.post.editPost

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import hello.kwfriends.firebase.realtimeDatabase.PostDetail

@Composable
fun EditPostPopup(
    state: Boolean,
    onDismiss: () -> Unit,
    editPostViewModel: EditPostViewModel,
    postDetail: PostDetail?
) {
    if (state && postDetail != null) {
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true)
        ) {
            BackHandler {
                onDismiss()
            }
            EditPostScreen(
                editPostViewModel = editPostViewModel,
                onDismiss = onDismiss,
                postDetail = postDetail
            )
        }
    }
}
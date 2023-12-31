package hello.kwfriends.ui.screens.post.setPostData

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import hello.kwfriends.firebase.realtimeDatabase.Action
import hello.kwfriends.firebase.realtimeDatabase.PostDetail

@Composable
fun SetPostDataPopup(
    state: Action,
    onDismiss: () -> Unit,
    setPostDataViewModel: SetPostDataViewModel,
    postDetail: PostDetail?
) {
    if (state != Action.NONE) {
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true)
        ) {
            BackHandler {
                onDismiss()
            }
            SetPostDataScreen(
                setPostDataViewModel = setPostDataViewModel,
                onDismiss = onDismiss,
                postDetail = postDetail,
                state = state
            )
        }
    }
}
package hello.kwfriends.ui.screens.post.setPostData

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import hello.kwfriends.firebase.realtimeDatabase.Action
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.ui.screens.home.HomeViewModel

@Composable
fun SetPostDataPopup(
    state: Boolean,
    onDismiss: () -> Unit,
    setPostDataViewModel: SetPostDataViewModel,
    homeViewModel: HomeViewModel,
    postDetail: PostDetail?
) {
    if (state && postDetail != null) {
        val action = homeViewModel.setPostDataState.third

        if (homeViewModel.setPostDataState.first) {
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
                    action = action
                )
            }
        }
    }
}
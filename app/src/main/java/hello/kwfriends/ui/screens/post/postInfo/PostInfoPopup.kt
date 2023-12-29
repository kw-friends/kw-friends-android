package hello.kwfriends.ui.screens.post.postInfo

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.ui.screens.home.HomeViewModel

@Composable
fun PostInfoPopup(
    state: Boolean,
    postDetail: PostDetail?,
    onDismiss: () -> Unit,
    onReport: () -> Unit,
    homeViewModel: HomeViewModel,
    enjoyButton: @Composable () -> Unit
) {
    if (state && postDetail != null) {
        Popup(
            onDismissRequest = onDismiss
        ) {
            BackHandler {
                onDismiss()
            }
            PostInfoScreen(
                postDetail = postDetail,
                onDismiss = onDismiss,
                onReport = onReport,
                homeViewModel = homeViewModel,
                enjoyButton = enjoyButton
            )
        }
    }
}
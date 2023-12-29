package hello.kwfriends.ui.screens.post.postInfo

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import androidx.compose.ui.window.Popup
import hello.kwfriends.ui.screens.home.HomeViewModel

@Composable
fun PostInfoPopup(
    state: Boolean,
    postDetail: PostDetail?,
    onDismiss: () -> Unit,
    onPostReport: () -> Unit,
    homeViewModel: HomeViewModel,
    enjoyButton: @Composable () -> Unit
) {
    if (state && postDetail != null) {
        Popup(
            onDismissRequest = onDismiss
        ) {
            BackHandler {
                if(homeViewModel.userInfoPopupState.first) {
                    homeViewModel.userInfoPopupState = false to ""
                }
                else {
                    onDismiss()
                }
            }
            PostInfoScreen(
                postDetail = postDetail,
                onDismiss = onDismiss,
                onPostReport = onPostReport,
                homeViewModel = homeViewModel,
                enjoyButton = enjoyButton
            )
        }
    }
}
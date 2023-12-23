package hello.kwfriends.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import androidx.compose.ui.window.Popup
import hello.kwfriends.ui.screens.home.HomeViewModel
import hello.kwfriends.ui.screens.postInfo.PostInfoScreen

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
                onReport = onReport,
                homeViewModel = homeViewModel,
                enjoyButton = enjoyButton
            )
        }
    }
}
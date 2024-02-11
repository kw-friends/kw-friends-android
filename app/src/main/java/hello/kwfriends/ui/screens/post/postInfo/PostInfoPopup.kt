package hello.kwfriends.ui.screens.post.postInfo

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import hello.kwfriends.ui.screens.main.MainViewModel

@Composable
fun PostInfoPopup(
    state: Boolean,
    postDetail: PostDetail?,
    onDismiss: () -> Unit,
    onPostReport: () -> Unit,
    onPostDelete: () -> Unit,
    mainViewModel: MainViewModel,
    mainNavigation: NavController,
    enjoyButton: @Composable () -> Unit
) {
    if (state && postDetail != null) {
        Popup(
            onDismissRequest = onDismiss
        ) {
            BackHandler {
                if (mainViewModel.userInfoPopupState.first) {
                    mainViewModel.userInfoPopupState = false to ""
                } else {
                    onDismiss()
                }
            }
            PostInfoScreen(
                postDetail = postDetail,
                onDismiss = onDismiss,
                onPostReport = onPostReport,
                onPostDelete = onPostDelete,
                mainViewModel = mainViewModel,
                enjoyButton = enjoyButton,
                mainNavigation = mainNavigation
            )
        }
    }
}
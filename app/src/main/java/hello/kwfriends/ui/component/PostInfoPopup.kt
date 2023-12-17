package hello.kwfriends.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import androidx.compose.ui.window.Popup
import hello.kwfriends.firebase.firestoreDatabase.PostDetail
import hello.kwfriends.ui.screens.postInfo.PostInfoScreen

@Composable
fun PostInfoPopup(
    state: Boolean,
    postDetail: PostDetail?,
    participantsCount: Int,
    onDismiss: () -> Unit,
    onReport: () -> Unit,
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
                participantsCount = participantsCount,
                onDismiss = onDismiss,
                onReport = onReport,
                enjoyButton = enjoyButton
            )
        }
    }
}
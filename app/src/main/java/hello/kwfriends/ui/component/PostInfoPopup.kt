package hello.kwfriends.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import androidx.compose.ui.window.Popup
import hello.kwfriends.ui.screens.postInfo.PostInfoScreen

@Composable
fun PostInfoPopup(
    state: Boolean,
    postDetail: PostDetail?,
    onDismiss: () -> Unit,
    participantsCountMap: SnapshotStateMap<String, Int>,
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
                participantsCountMap = participantsCountMap,
                onDismiss = onDismiss,
                onReport = onReport,
                enjoyButton = enjoyButton
            )
        }
    }
}
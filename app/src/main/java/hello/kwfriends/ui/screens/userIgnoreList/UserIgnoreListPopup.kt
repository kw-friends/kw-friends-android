package hello.kwfriends.ui.screens.userIgnoreList

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup

@Composable
fun UserIgnoreListPopup(
    state: Boolean,
    onDismiss: () -> Unit,
    downloadUri: (String) -> Unit,
    downloadData: (String) -> Unit,
    removeUserIgnore: (String) -> Unit,
    onUserInfoPopup: (String) -> Unit
) {
    if(state) {
        Popup(
            onDismissRequest = onDismiss
        ) {
            UserIgnoreListScreen(
                onDismiss = onDismiss,
                downloadUri = downloadUri,
                downloadData = downloadData,
                removeUserIgnore = removeUserIgnore,
                onUserInfoPopup = onUserInfoPopup
            )
        }
    }
}
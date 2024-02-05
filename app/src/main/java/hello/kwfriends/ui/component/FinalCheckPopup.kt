package hello.kwfriends.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup

@Composable
fun FinalCheckPopup(
    state: Boolean,
    title: String,
    body: String,
    onContinue: () -> Unit,
    onDismiss: () -> Unit
) {
    Popup(
        onDismissRequest = onDismiss
    ) {
        BackHandler {
            onDismiss()
        }
        if (state) {
            FinalCheckScreen(
                onDismiss = onDismiss,
                title = title,
                body = body,
                onContinue = onContinue
            )
        }
    }
}
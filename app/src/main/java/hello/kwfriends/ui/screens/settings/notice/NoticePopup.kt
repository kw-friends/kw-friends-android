package hello.kwfriends.ui.screens.settings.notice

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import hello.kwfriends.ui.screens.settings.SettingsViewModel

@Composable
fun NoticePopup(
    state: Boolean,
    onDismiss: () -> Unit,
    settingsViewModel: SettingsViewModel
) {
    if (state) {
        Popup(
            onDismissRequest = onDismiss
        ) {
            NoticeScreen(
                settingsViewModel = settingsViewModel,
                notices = settingsViewModel.noticeLists
            )
        }
    }
}
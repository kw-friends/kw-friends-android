package hello.kwfriends.ui.screens.main

sealed class MainUiState {
    object Home : MainUiState()
    object Settings : MainUiState()
    object MyPage : MainUiState()
}
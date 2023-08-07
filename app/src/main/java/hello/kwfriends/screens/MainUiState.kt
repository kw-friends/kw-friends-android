package hello.kwfriends.screens

sealed class MainUiState {
    object Home : MainUiState()
    object Settings: MainUiState()
    object MyPage: MainUiState()
}
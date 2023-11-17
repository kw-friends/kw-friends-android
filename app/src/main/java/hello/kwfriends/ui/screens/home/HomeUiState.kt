package hello.kwfriends.ui.screens.home

sealed class HomeUiState {
    object Home : HomeUiState()
    object NewPost: HomeUiState()
}
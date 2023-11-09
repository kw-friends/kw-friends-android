package hello.kwfriends.ui.splash

sealed class SplashState {
    object Done: SplashState()
    object Waiting: SplashState()
}
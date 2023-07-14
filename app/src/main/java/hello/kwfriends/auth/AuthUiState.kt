package hello.kwfriends.auth

sealed class AuthUiState{
    object Loading: AuthUiState() // 로딩 화면
    object Menu: AuthUiState() // 로그인, 회원가입 선택 화면
    object SignIn: AuthUiState() // 로그인화면
    object Register: AuthUiState() // 회원가입 화면
    object RequestEmailVerify: AuthUiState() // 이메일 인증 요청 화면
    object InputUserInfo: AuthUiState() // 정보 입력 화면
    object SignInSuccess: AuthUiState() // 로그인 성공 화면

}
package hello.kwfriends.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel: ViewModel(){

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Menu)

}
package hello.kwfriends.ui.screens.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var uiState by mutableStateOf<MainUiState>(MainUiState.Home)

    fun onClickedMyPage() {
        viewModelScope.launch {
            uiState = MainUiState.MyPage
            Log.d("minmul", "onClickedMyPage")
        }
    }

    fun onClickedSettings() {
        viewModelScope.launch {
            uiState = MainUiState.Settings
            Log.d("minmul", "onClickedSettings")
        }
    }

    fun onClickedFindGathering() {
        viewModelScope.launch {
            uiState = MainUiState.Home
            Log.d("minmul", "onClickedHome")
        }
    }
}
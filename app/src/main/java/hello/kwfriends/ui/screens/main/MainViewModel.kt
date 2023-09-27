package hello.kwfriends.ui.screens.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hello.kwfriends.firebase.firestoreManager.PostDetail
import hello.kwfriends.firebase.firestoreManager.PostManager
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var uiState by mutableStateOf<MainUiState>(MainUiState.Home)

    var posts by mutableStateOf<List<PostDetail>>(listOf())


    fun goToNewPostPage() {
        viewModelScope.launch {
            uiState = MainUiState.NewPost
            Log.d("minmul", "goToNewPostPage")
        }
    }

    fun goToHome() {
        viewModelScope.launch {
            uiState = MainUiState.Home
            Log.d("minmul", "goToHome")
        }
    }

    fun getPostFromFirestore() {
        Log.d("getPostFromFirestore()",  "데이터 가져옴")
        viewModelScope.launch {
            posts = PostManager.getPostRef()
        }
    }
}
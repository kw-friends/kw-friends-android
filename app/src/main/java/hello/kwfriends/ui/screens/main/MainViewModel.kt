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
    var posts by mutableStateOf<List<PostDetail>>(listOf())

    fun getPostFromFirestore() {
        Log.d("getPostFromFirestore()",  "데이터 가져옴")
        viewModelScope.launch {
            posts = PostManager.getPostRef()
        }
    }

    fun participateOnGathering(target: String) {
        viewModelScope.launch {
            PostManager.updateParticipationState(target)
        }
    }
}
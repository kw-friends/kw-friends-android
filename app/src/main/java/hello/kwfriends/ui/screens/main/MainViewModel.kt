package hello.kwfriends.ui.screens.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hello.kwfriends.firebase.firestoreManager.ParticipationStatus
import hello.kwfriends.firebase.firestoreManager.PostDetail
import hello.kwfriends.firebase.firestoreManager.PostManager
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var posts by mutableStateOf<List<PostDetail>>(listOf())
    var participationStatusMap = mutableStateMapOf<String, String>()
    var currentParticipationStatusMap = mutableStateMapOf<String, Int>()



    fun participationStatusMapInit(postID: String, status: String) {
        participationStatusMap[postID] = if (status == ParticipationStatus.PARTICIPATED) {
            ParticipationStatus.PARTICIPATED
        } else {
            ParticipationStatus.NOT_PARTICIPATED
        }
    }

    fun currentParticipationStatusMapUpdate(postID: String, add: Int){
        currentParticipationStatusMap[postID] = currentParticipationStatusMap[postID]!! + add
    }

    fun currentParticipationStatusMapInit(postID: String, count: Int) {
        currentParticipationStatusMap[postID] = count
    }

    fun updateParticipationStatus(postID: String, viewModel: MainViewModel) {
        viewModelScope.launch {
            if (participationStatusMap[postID] == ParticipationStatus.NOT_PARTICIPATED) {
                participationStatusMap[postID] = ParticipationStatus.GETTING_IN
                PostManager.updateParticipationState(target = postID, viewModel = viewModel).also {
                    participationStatusMap[postID] = ParticipationStatus.PARTICIPATED
                }
            } else {
                participationStatusMap[postID] = ParticipationStatus.GETTING_OUT
                PostManager.updateParticipationState(target = postID, viewModel = viewModel).also {
                    participationStatusMap[postID] = ParticipationStatus.NOT_PARTICIPATED
                }
            }
        }
    }

    fun getPostFromFirestore(viewModel: MainViewModel) {
        Log.d("getPostFromFirestore()", "데이터 가져옴")
        viewModelScope.launch {
            posts = PostManager.getPostRef(viewModel = viewModel)
        }
    }
}
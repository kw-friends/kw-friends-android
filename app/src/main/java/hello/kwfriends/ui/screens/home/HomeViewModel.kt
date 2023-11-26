package hello.kwfriends.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.PostDetail_
import hello.kwfriends.firebase.realtimeDatabase.Post_
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var posts by mutableStateOf<List<PostDetail_>>(listOf())
    var participationStatusMap = mutableStateMapOf<String, ParticipationStatus>()
    var currentParticipationStatusMap = mutableStateMapOf<String, Int>()

    //모임 새로고침 상태 저장 변수
    var isRefreshing by mutableStateOf(false)

    fun initPostMap() {
        viewModelScope.launch {
            posts = Post_.initPostData()
        }
    }

    fun currentParticipationStatusMapUpdate(postID: String, add: Int) {
        currentParticipationStatusMap[postID] = currentParticipationStatusMap[postID]!! + add
    }

    fun currentParticipationStatusMapInit(postID: String, count: Int) {
        currentParticipationStatusMap[postID] = count
    }

    fun updateParticipationStatus(postID: String, viewModel: HomeViewModel) {
        viewModelScope.launch {
            if (participationStatusMap[postID] == ParticipationStatus.NOT_PARTICIPATED) {
                participationStatusMap[postID] = ParticipationStatus.GETTING_IN
               Post_.updateParticipationStatus(target = postID, viewModel = viewModel).also {
                    participationStatusMap[postID] = ParticipationStatus.PARTICIPATED
                }
            } else {
                participationStatusMap[postID] = ParticipationStatus.GETTING_OUT
                Post_.updateParticipationStatus(target = postID, viewModel = viewModel).also {
                    participationStatusMap[postID] = ParticipationStatus.NOT_PARTICIPATED
                }
            }
        }
    }

    fun refreshPost() {
        viewModelScope.launch {
            isRefreshing = true
            getPostFromFirestore()
            isRefreshing = false
        }
    }

    //포스트 목록 및 세부 정보 불러오는 함수
    suspend fun getPostFromFirestore(): Boolean {
        posts = Post_.initPostData()
        Log.w("Lim", "게시글 불러오기 성공")
        Log.w("Lim", "게시글 불러오기 실패(게시글이 없거나 불러오지 못함)")
        return false
    }

}
package hello.kwfriends.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.realtimeDatabase.Action
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.Post
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.firebase.realtimeDatabase.UserData
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var posts by mutableStateOf<List<PostDetail>>(listOf())
    var participationStatusMap = mutableStateMapOf<String, ParticipationStatus>()
    var participantsCountMap = mutableStateMapOf<String, Int>()

    //모임 새로고침 상태 저장 변수
    var isRefreshing by mutableStateOf(false)

    private val uid = Firebase.auth.currentUser!!.uid

    fun initPostMap() {
        viewModelScope.launch {
            posts = Post.initPostData()
            Log.d("initPostMap", "post set to ${posts}")

            for (post in posts) {
                participationStatusMap[post.postID] = if (uid in post.participants.keys) {
                    ParticipationStatus.PARTICIPATED
                } else {
                    ParticipationStatus.NOT_PARTICIPATED
                }

                participantsCountMap[post.postID] = post.participants.count()
            }
        }
    }

    fun updateParticipationStatus(postID: String, viewModel: HomeViewModel) {
        val postDetail = posts.find { it.postID == postID }
        Log.d("updateParticipationStatus", "myParticipantStatus of $postID is ${postDetail?.myParticipantStatus}")
        viewModelScope.launch {
            if (postDetail?.myParticipantStatus == ParticipationStatus.NOT_PARTICIPATED) {
                participationStatusMap[postID] = ParticipationStatus.GETTING_IN
                val result =
                    Post.updateParticipationStatus(postID = postID, action = Action.ADD)
                if (result) {
                    val updatedPostDetail = postDetail.copy(myParticipantStatus = ParticipationStatus.PARTICIPATED)
                    posts = posts.map { if (it.postID == postID) updatedPostDetail else it }

                    participationStatusMap[postID] = ParticipationStatus.PARTICIPATED
                    participantsCountMap[postID] = participantsCountMap[postID]!! + 1
                } else {
                    participationStatusMap[postID] = ParticipationStatus.NOT_PARTICIPATED
                }
            } else { // postDetail?.myParticipantStatus == ParticipationStatus.PARTICIPATED
                participationStatusMap[postID] = ParticipationStatus.GETTING_OUT
                val result =
                    Post.updateParticipationStatus(postID = postID, action = Action.DELETE)
                if (result) {
                    val updatedPostDetail = postDetail!!.copy(myParticipantStatus = ParticipationStatus.NOT_PARTICIPATED)
                    posts = posts.map { if (it.postID == postID) updatedPostDetail else it }

                    participationStatusMap[postID] = ParticipationStatus.NOT_PARTICIPATED
                    participantsCountMap[postID] = participantsCountMap[postID]!! - 1
                } else {
                    participationStatusMap[postID] = ParticipationStatus.PARTICIPATED
                }
            }
        }
    }

    fun refreshPost() {
        viewModelScope.launch {
            isRefreshing = true
            Post.initPostData()
            isRefreshing = false
        }
    }

    //포스트 목록 및 세부 정보 불러오는 함수
    suspend fun getPostFromFirestore(): Boolean {
        posts = Post.initPostData()
        Log.w("getPostFromFirestore", "게시글 불러옴")
        return false
    }
}
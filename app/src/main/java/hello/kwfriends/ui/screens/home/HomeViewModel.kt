package hello.kwfriends.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import hello.kwfriends.firebase.authentication.UserAuth
import hello.kwfriends.firebase.firestoreDatabase.ParticipationStatus
import hello.kwfriends.firebase.firestoreDatabase.PostDetail
import hello.kwfriends.firebase.firestoreDatabase.PostManager
import hello.kwfriends.firebase.firestoreDatabase.PostManager.getParticipantsDetail
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var posts by mutableStateOf<List<PostDetail>>(listOf())
    var participationStatusMap = mutableStateMapOf<String, ParticipationStatus>()
    var currentParticipationStatusMap = mutableStateMapOf<String, Int>()
    //모임 새로고침 상태 저장 변수
    var isRefreshing by mutableStateOf(false)

    fun participationStatusMapInit(postID: String, status: ParticipationStatus) {
        participationStatusMap[postID] = if (status == ParticipationStatus.PARTICIPATED) {
            ParticipationStatus.PARTICIPATED
        } else {
            ParticipationStatus.NOT_PARTICIPATED
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

    fun refreshPost() {
        viewModelScope.launch {
            isRefreshing = true
            getPostFromFirestore()
            isRefreshing = false
        }
    }

    //포스트 목록 및 세부 정보 불러오는 함수
    suspend fun getPostFromFirestore(): Boolean {
        Log.d("getPostFromFirestore()", "데이터 가져옴")
        val documents = PostManager.getPostDocuments()
        if(documents != null){
            posts = analysisPost(documents)
            Log.w("Lim", "게시글 불러오기 성공")
            return true
        }
        Log.w("Lim", "게시글 불러오기 실패(게시글이 없거나 불러오지 못함)")
        return false
    }

    //포스트 세부 정보 추출 함수
    suspend fun analysisPost(postsRes: QuerySnapshot): List<PostDetail> {
        return postsRes.documents.map { document ->
            val participantsDetail = getParticipantsDetail(document)
            val participantsCount = participantsDetail.size()
            val participantStatus =
                if (PostManager.isDocumentExist(participantsDetail, UserAuth.fa.uid.toString())) {
                    ParticipationStatus.PARTICIPATED
                } else {
                    ParticipationStatus.NOT_PARTICIPATED
                }

            participationStatusMapInit(document.id, participantStatus)
            currentParticipationStatusMapInit(document.id, participantsCount)

            PostDetail(
                gatheringTitle = document.getString("gatheringTitle") ?: "",
                gatheringPromoter = document.getString("gatheringPromoter") ?: "",
                gatheringLocation = document.getString("gatheringLocation") ?: "",
                gatheringTime = document.getString("gatheringTime") ?: "",
                maximumParticipants = document.getString("maximumParticipants") ?: "",
                minimumParticipants = document.getString("minimumParticipants") ?: "",
                currentParticipants = participantsCount.toString(),
                gatheringDescription = document.getString("gatheringDescription") ?: "",
                participantStatus = participantStatus,
                postID = document.id
            )
        }
    }

}
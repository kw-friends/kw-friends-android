package hello.kwfriends.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import hello.kwfriends.Tags.Tags
import hello.kwfriends.firebase.authentication.UserAuth
import hello.kwfriends.firebase.firestoreDatabase.ParticipationStatus
import hello.kwfriends.firebase.firestoreDatabase.PostDetail
import hello.kwfriends.firebase.firestoreDatabase.PostManager
import hello.kwfriends.firebase.firestoreDatabase.PostManager.getParticipantsDetail
import hello.kwfriends.realtimeDatabase.Report
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var posts by mutableStateOf<List<PostDetail>>(listOf())
    var searchingPosts by mutableStateOf<List<PostDetail>>(listOf())
    var participationStatusMap = mutableStateMapOf<String, ParticipationStatus>()
    var currentParticipationStatusMap = mutableStateMapOf<String, Int>()
    //모임 새로고침 상태 저장 변수
    var isRefreshing by mutableStateOf(false)
    //검색 상태 저장 변수
    var isSearching by mutableStateOf(false)
    //검색 텍스트 저장 변수
    var searchText by mutableStateOf("")
    //태그 저장 변수
    var filterTagMap = mutableStateMapOf<String, Boolean>().apply {
        Tags.list.forEach { tag ->
            this[tag] = false
        }
    }
    //신고 다이얼로그 보이기 여부 및 신고 대상 포스트 uid
    var reportDialogState by mutableStateOf<Pair<Boolean, String?>>(false to null)
    //신고 텍스트 리스트
    val reportTextList by mutableStateOf(
        listOf(
            "게시판 성격에 부적절함",
            "낚시/놀람/도배",
            "음란물/불건전한 만남 및 대화",
            "불쾌감을 주는 사용자",
            "정당/정치인 비하 및 선거운동",
            "유출/사칭/사기",
            "상업적 광고 및 판매",
            "욕설/비하"
        )
    )
    var reportChoice by mutableStateOf<MutableList<String>>(mutableListOf())

    fun initReportChoice() {
        reportChoice = mutableListOf()
    }

    //검색 텍스트 수정 함수
    fun setSearchContentText(text: String) {
        searchText = text
        if(isSearching) {
            searchingPosts = search(posts)
        }
    }

    fun report() {
        viewModelScope.launch {
            reportDialogState = false to reportDialogState.second
            Report.report(
                postID = reportDialogState.second!!,
                reporterID = UserAuth.fa.currentUser!!.uid,
                reason = reportChoice
            )
            reportDialogState = false to null
        }
    }

    //필터 함수
    fun filter(targetPosts: List<PostDetail>): List<PostDetail> {
        val activityTags = mutableListOf<String>()
        filterTagMap.forEach{
            if(it.value){
                activityTags.add(it.key)
            }
        }
        val resultPosts = targetPosts.filter { post ->
            activityTags.all {
                it in post.gatheringTags
            }
        }
        return resultPosts
    }

    fun search(targetPosts: List<PostDetail>): List<PostDetail> {
        /* TODO 검색 알고리즘 최적화 */
        Log.w("Lim", "Searching")
        if(searchText == "") {
            return listOf()
        }
        val resultPosts = targetPosts.filter { post ->
            post.gatheringTitle.contains(searchText, ignoreCase = true) || //제목
            post.gatheringLocation.contains(searchText, ignoreCase = true) || //장소
            post.gatheringTime.contains(searchText, ignoreCase = true) || //시간
            post.gatheringDescription.contains(searchText, ignoreCase = true) || //설명
            post.participantStatus.toString().contains(searchText, ignoreCase = true) //상태
        }
        return resultPosts
    }

    fun onclickSearchButton() {
        if(!isSearching) {
            isSearching = true
        }
    }

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
                gatheringTags = document.data?.get("gatheringTags") as? List<String> ?: listOf(),
                postID = document.id
            )
        }
    }

}
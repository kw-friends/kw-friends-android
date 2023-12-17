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
    var participantsCountMap = mutableStateMapOf<String, Int>()

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
    //포스트 생성 다이얼로그 보이기 여부
    var newPostPopupState by mutableStateOf<Boolean>(false)
    //포스트 다이얼로그 보이기 여부 및 포스트 uid
    var postPopupState by mutableStateOf<Pair<Boolean, PostDetail?>>(false to null)
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
    /*
    TODO 1. 신고시 post에 신고자 uid 리스트 추가
    TODO 2. 신고수 일정 이상 되면 알림 or 숨기기
    TODO 3. 신고 한번만 할 수 있도록 하기(신고 버튼 오른쪽에 체크표시)
    */
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
            post.participantStatus.toString().contains(searchText, ignoreCase = true) || //상태
            post.gatheringTags.toString().contains(searchText, ignoreCase = true)
        }
        return resultPosts
    }

    fun onclickSearchButton() {
        if(!isSearching) {
            isSearching = true
        }
    }

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
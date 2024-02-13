package hello.kwfriends.ui.screens.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.Tags.Tags
import hello.kwfriends.firebase.authentication.UserAuth
import hello.kwfriends.firebase.realtimeDatabase.Action
import hello.kwfriends.firebase.realtimeDatabase.Chattings
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.firebase.realtimeDatabase.Post
import hello.kwfriends.firebase.realtimeDatabase.PostDetail
import hello.kwfriends.firebase.realtimeDatabase.Report
import hello.kwfriends.firebase.realtimeDatabase.UserData
import hello.kwfriends.firebase.storage.ProfileImage
import hello.kwfriends.preferenceDatastore.UserDataStore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MainViewModel : ViewModel() {
    var posts by mutableStateOf<List<PostDetail>>(listOf())
    var searchingPosts by mutableStateOf<List<PostDetail>>(listOf())
    var participatedGatherings by mutableStateOf<List<Pair<PostDetail, ParticipationStatus>>>(listOf())

    //모임 새로고침 상태 저장 변수
    var isRefreshing by mutableStateOf(false)

    //검색 상태 저장 변수
    var isSearching by mutableStateOf(false)

    // 참여 상태 업데이트 중 변수
    var isProcessing by mutableStateOf(false)

    //검색 텍스트 저장 변수
    var searchText by mutableStateOf("")

    //태그 저장 변수
    var filterTagMap = mutableStateMapOf<String, Boolean>().apply {
        Tags.list.forEach { tag ->
            this[tag] = false
        }
    }

    // 현재 참여 중 모임만 표시
    var onlyParticipatedGathering by mutableStateOf(false)

    //포스트 팝업 보이기 여부 및 포스트 uid
    var postInfoPopupState by mutableStateOf<Pair<Boolean, String>>(false to "")

    //유저 정보 팝업 보이기 여부 및 포스트 uid
    var userInfoPopupState by mutableStateOf<Pair<Boolean, String>>(false to "")

    //포스트 신고 팝업 보이기 여부 및 신고 대상 포스트 id
    var postReportDialogState by mutableStateOf<Pair<Boolean, String>>(false to "")

    // 포스트 설정 페이지 다이얼로그 보이기 여부, 포스트 uid, Action State
    var setPostDataState by mutableStateOf<Pair<Action, String>>(Action.NONE to "")

    //유저 신고 팝업 보이기 여부 및 신고 대상 uid
    var userReportDialogState by mutableStateOf<Pair<Boolean, String>>(false to "")

    // 사용자 조작 확인 다이얼로그
    var finalCheckState by mutableStateOf(false)
    var finalCheckTitle by mutableStateOf("")
    var finalCheckBody by mutableStateOf("")
    var onContinueAction by mutableStateOf({}) // 제거

    // 이벤트 로딩 성공 여부
    var eventLoaded by mutableStateOf(false)

    //포스트 신고 텍스트 리스트
    val postReportTextList by mutableStateOf(
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

    //유저 신고 텍스트 리스트
    val userReportTextList by mutableStateOf(
        listOf(
            "낚시/놀람/도배",
            "음란물/불건전한 만남 및 대화",
            "불쾌감을 주는 사용자",
            "정당/정치인 비하 및 선거운동",
            "유출/사칭/사기",
            "상업적 광고 및 판매",
            "욕설/비하"
        )
    )

    private val uid = Firebase.auth.currentUser!!.uid

    // post 리스너 설정
    init {
        Post.setPostListener(viewModel = this, action = Action.ADD)
    }

    //유저 차단 추가
    fun addUserIgnore(uid: String) {
        viewModelScope.launch {
            UserDataStore.userIgnoreListUpdate("ADD", uid)
            UserDataStore.setStringSetData("USER_IGNORE_LIST", UserDataStore.userIgnoreList)
            Log.w("addUserIgnore", "유저($uid) 차단")
        }
    }

    //유저 차단 제거
    fun removeUserIgnore(uid: String) {
        viewModelScope.launch {
            UserDataStore.userIgnoreListUpdate("REMOVE", uid)
            UserDataStore.setStringSetData("USER_IGNORE_LIST", UserDataStore.userIgnoreList)
            Log.w("removeUserIgnore", "유저($uid) 차단해제")
        }
    }

    //검색 텍스트 수정 함수
    fun setSearchContentText(text: String) {
        searchText = text
        if (isSearching) {
            searchingPosts = search(posts)
        }
    }

    fun postReport(reason: List<String>) {
        viewModelScope.launch {
            postReportDialogState = false to postReportDialogState.second
            Report.postReport(
                postID = postReportDialogState.second,
                postProviderID = posts.find { it.postID == (postReportDialogState.second) }?.gatheringPromoterUID
                    ?: "unknown",
                reporterID = UserAuth.fa.currentUser!!.uid,
                reason = reason
            )
            postReportDialogState = false to ""
        }
    }

    fun postDelete(postID: String) {
        viewModelScope.launch {
            Post.deletePost(postID)
            finalCheckPopupSet(title = "", body = "", onContinueAction = {})
            postInfoPopupState = false to ""
        }
    }

    fun userReport(reason: List<String>) {
        viewModelScope.launch {
            userReportDialogState = false to userReportDialogState.second
            Report.userReport(
                uid = userReportDialogState.second,
                reporterID = Firebase.auth.currentUser?.uid ?: "unknown",
                reason = reason,
            )
            downlodData(userReportDialogState.second)
            userReportDialogState = false to ""
        }
    }

    fun dateTimeFormat(time: Long): String {
        return SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault()).format(time)
    }

    //필터 함수
    fun filter(targetPosts: List<PostDetail>): List<PostDetail> {
        val activityTags = mutableListOf<String>()
        filterTagMap.forEach {
            if (it.value) {
                activityTags.add(it.key)
            }
        }
        val resultPosts = targetPosts.filter { post ->
            activityTags.all {
                it in post.gatheringTags
            }
        }.filter { post ->
            if (onlyParticipatedGathering) uid in post.participants else true
        }
        return resultPosts
    }

    fun search(targetPosts: List<PostDetail>): List<PostDetail> {
        /* TODO 검색 알고리즘 최적화 */
        Log.w("Lim", "Searching")
        if (searchText == "") {
            return listOf()
        }
        val resultPosts = targetPosts.filter { post ->
            post.gatheringTitle.contains(searchText, ignoreCase = true) || //제목
                    post.gatheringLocation.contains(searchText, ignoreCase = true) || //장소
//                    post.gatheringTime.contains(searchText, ignoreCase = true) || //시간
                    post.gatheringDescription.contains(searchText, ignoreCase = true) || //설명
                    post.gatheringTags.toString().contains(searchText, ignoreCase = true)
        }
        return resultPosts
    }

    fun onclickSearchButton() {
        if (!isSearching) {
            isSearching = true
        }
    }

    fun finalCheckPopupSet(
        title: String,
        body: String,
        onContinueAction: () -> Unit
    ) {
        finalCheckTitle = title
        finalCheckBody = body
        this.onContinueAction = onContinueAction
    }

    fun updateParticipatedGatherings() {
        participatedGatherings = posts.filter { uid in it.participants }.map {
            Pair(it, ParticipationStatus.PARTICIPATED)
        }
    }

    fun postAdded(postData: PostDetail, postID: String) {
        postData.postID = postID
        postData.myParticipantStatus = if (postData.gatheringPromoterUID == uid) {
            ParticipationStatus.MY_GATHERING
        } else if (uid in postData.participants.keys) {
            ParticipationStatus.PARTICIPATED
        } else if (postData.participants.count() >= postData.maximumParticipants.toInt()) {
            ParticipationStatus.MAXED_OUT
        } else {
            ParticipationStatus.NOT_PARTICIPATED
        }

        posts += postData
        Log.d(
            "postAdded",
            "${postData.participants.count()}}"
        )
        Log.d(
            "postAdded",
            "${postData.myParticipantStatus}}"
        )
        Log.d("postAdded", "${postData.participants.toMap()}")
        Log.d("postAdded", "postID: ${postData.postID}")

        updateParticipatedGatherings()
    }

    fun postRemoved(postID: String) {
        posts = posts.filter { it.postID != postID }
        Log.d("postRemoved", "postID: $postID")

        updateParticipatedGatherings()
    }

    fun postChanged(postData: PostDetail, postID: String) {
        Log.d("postChanged", "$postData")
        postData.postID = postID
        postData.myParticipantStatus = if (postData.gatheringPromoterUID == uid) {
            ParticipationStatus.MY_GATHERING
        } else if (uid in postData.participants.keys) {
            ParticipationStatus.PARTICIPATED
        } else if (postData.participants.count() >= postData.maximumParticipants.toInt()) {
            ParticipationStatus.MAXED_OUT
        } else {
            ParticipationStatus.NOT_PARTICIPATED
        }
        posts = posts.map { if (it.postID == postID) postData else it }
        Log.d("postChanged", "postID: ${postData.postID}")
        Log.d("postChanged", "posts: $posts")

        updateParticipatedGatherings()
    }

    fun initPostMap() {
        viewModelScope.launch {
            posts = Post.initPostData()
            Log.d("initPostMap", "post set to $posts")

            for (post in posts) {
                if (uid in post.participants.keys) {
                    ParticipationStatus.PARTICIPATED
                } else if (post.participants.count() >= post.maximumParticipants.toInt()) {
                    ParticipationStatus.MAXED_OUT
                } else {
                    ParticipationStatus.NOT_PARTICIPATED
                }
            }
        }

        updateParticipatedGatherings()
    }

    fun updateParticipationStatus(postID: String) {
        val postDetail = posts.find { it.postID == postID }

        Log.d(
            "updateParticipationStatus",
            "myParticipantStatus of $postID is ${postDetail?.myParticipantStatus}"
        )
        isProcessing = true

        viewModelScope.launch {
            // 참여 신청
            if (postDetail?.myParticipantStatus == ParticipationStatus.NOT_PARTICIPATED) {
                postDetail.myParticipantStatus = ParticipationStatus.GETTING_IN

                val result =
                    Post.updateParticipationStatus(
                        postID = postID,
                        chattingRoomID = postDetail.chattingRoomID,
                        action = Action.ADD
                    )
                if (result) {
                    postDetail.myParticipantStatus = ParticipationStatus.PARTICIPATED
                } else {
                    postDetail.myParticipantStatus = ParticipationStatus.NOT_PARTICIPATED
                }
                return@launch
            }

            // 참여 취소
            if (postDetail?.myParticipantStatus == ParticipationStatus.PARTICIPATED) {
                postDetail.myParticipantStatus = ParticipationStatus.GETTING_OUT

                val result =
                    Post.updateParticipationStatus(
                        postID = postID,
                        chattingRoomID = postDetail.chattingRoomID,
                        action = Action.DELETE
                    )
                if (result) {
                    postDetail.myParticipantStatus = ParticipationStatus.NOT_PARTICIPATED
                } else {
                    postDetail.myParticipantStatus = ParticipationStatus.PARTICIPATED
                }
                return@launch
            }
        }

        updateParticipatedGatherings()

        isProcessing = false
    }

    fun refreshPost() {
        viewModelScope.launch {
            isRefreshing = true
            posts = Post.initPostData()
            initPostMap()
            isRefreshing = false
        }
    }

    fun downlodUri(uid: String) {
        viewModelScope.launch {
            val uri = ProfileImage.getDownloadUrl(uid)
            ProfileImage.updateUsersUriMap(uid, uri)
        }
    }

    fun downlodData(uid: String) {
        viewModelScope.launch {
            val data = UserData.get(uid)
            UserData.updateUsersDataMap(uid, data)
        }
    }

    fun makeDirectChatting(targetUid: String, mainNavigation: NavController) {
        viewModelScope.launch {
            val roomID = Chattings.makeDirectChatting(targetUid)
            Chattings.getRoomList()
            if (roomID != "") mainNavigation.navigate(Routes.CHATTING_SCREEN + "/${roomID}")
        }
    }

    fun joinGroupChatting(
        postDetail: PostDetail,
        gotoChattingRoom: () -> Unit
    ) {
        viewModelScope.launch {
            Chattings.getRoomList()
            if (Chattings.join(postDetail.chattingRoomID)) {
                gotoChattingRoom()
            }
        }
    }
}
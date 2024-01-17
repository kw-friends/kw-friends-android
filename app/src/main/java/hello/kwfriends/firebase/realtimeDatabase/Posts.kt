package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import hello.kwfriends.ui.screens.main.MainViewModel
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class PostDetail(
    val gatheringTitle: String = "",
    val gatheringPromoterUID: String = "",
    val gatheringPromoter: String = "",
    val gatheringLocation: String = "",
    val gatheringTime: Long = 0L,
    val maximumParticipants: String = "",
    val gatheringDescription: String = "",
    var myParticipantStatus: ParticipationStatus = ParticipationStatus.NOT_PARTICIPATED,
    var postID: String = "",
    var timestamp: Any = "",
    val gatheringTags: List<String> = emptyList(),
    var reporters: Map<String, Any> = emptyMap(),
    var participants: Map<String, Any> = emptyMap(),
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "gatheringTitle" to gatheringTitle,
            "gatheringPromoter" to gatheringPromoter,
            "gatheringPromoterUID" to gatheringPromoterUID,
            "gatheringLocation" to gatheringLocation,
            "gatheringTime" to gatheringTime,
            "maximumParticipants" to maximumParticipants,
            "gatheringDescription" to gatheringDescription,
            "gatheringTags" to gatheringTags,
            "timestamp" to timestamp
        )
    }
}

enum class ParticipationStatus {
    PARTICIPATED,
    NOT_PARTICIPATED,
    GETTING_IN,
    GETTING_OUT,
    MAXED_OUT,
    MY_GATHERING
}

enum class Action {
    ADD,
    DELETE,
    MODIFY,
    NONE
}

object Post {

    private var database = Firebase.database
    private val uid = Firebase.auth.currentUser!!.uid

    fun setPostListener(viewModel: MainViewModel?, action: Action) {
        val postReference = database.getReference("posts")
        val postListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(
                    "postListener.onChildAdded",
                    "onChildAdded:${dataSnapshot.value}, postID: ${dataSnapshot.key!!}"
                )
                val postDetail = dataSnapshot.getValue(PostDetail::class.java) ?: return
                viewModel?.postAdded(postData = postDetail, postID = dataSnapshot.key ?: return)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(
                    "postListener.onDataChanged",
                    "onDataChanged, ${dataSnapshot.key}, ${dataSnapshot.value}, $previousChildName"
                )
                val postDetail = dataSnapshot.getValue(PostDetail::class.java) ?: return
                viewModel?.postChanged(postData = postDetail, postID = dataSnapshot.key ?: return)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(
                    "postListener.onChildRemoved",
                    "onChildRemoved:${dataSnapshot.value!!}, postID: ${dataSnapshot.key!!}"
                )
                val postDetail = dataSnapshot.getValue(PostDetail::class.java) ?: return
                viewModel?.postRemoved(postID = dataSnapshot.key!!)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("postListener.onChildMoved", "onChildMoved:" + dataSnapshot.key!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(
                    "postListener.onCancelled",
                    "loadPost:onCancelled",
                    databaseError.toException()
                )
            }
        }
        if (action == Action.ADD) {
            postReference.addChildEventListener(postListener)
            Log.d("setPostListener", "setPostListener 시작")
        } else { // action == Action.DELETE
            postReference.removeEventListener(postListener)
            Log.d("setPostListener", "setPostListener 종료")
        }

    }


    suspend fun initPostData(): MutableList<PostDetail> {
        val posts = database.reference.child("posts").get()
            .addOnSuccessListener {
                Log.d("initPostData", "모임 데이터 가져오기 성공 $it")
            }
            .addOnFailureListener { e ->
                Log.d("initPostData", "모임 데이터 가져오기 실패: $e")
            }.await()

        val postList = mutableListOf<PostDetail>()
        for (postSnapshot in posts.children) {
            val postID = postSnapshot.key ?: continue
            val postDetail = postSnapshot.getValue(PostDetail::class.java)
            if (postDetail != null) {
                postDetail.postID = postID

                postDetail.myParticipantStatus = if (postDetail.gatheringPromoterUID == uid) {
                    ParticipationStatus.MY_GATHERING
                } else if (uid in postDetail.participants.keys) {
                    ParticipationStatus.PARTICIPATED
                } else if (postDetail.participants.count() >= postDetail.maximumParticipants.toInt()) {
                    ParticipationStatus.MAXED_OUT
                } else {
                    ParticipationStatus.NOT_PARTICIPATED
                }
            }
            postDetail?.let { postList.add(it) }
        }

        Log.d("initPostData", "모임 데이터 변환 완료")

        return postList
    }

    suspend fun deletePost(postID: String) {
        val result = suspendCoroutine<Boolean> { continuation ->
            database.reference.child("/posts/$postID").setValue(null)
                .addOnSuccessListener {
                    Log.d("deletePost", "postId \"${postID}\" 삭제 완료")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    Log.d("deletePost", "postId \"${postID}\" 삭제 실패: $e")
                    continuation.resume(false)
                }
        }
    }


    suspend fun upload(postData: Map<String, Any>) {
        val key = database.reference.child("posts").push().key
        val postHashMap = hashMapOf<String, Any>(
            "/posts/$key" to postData,
        )

        val result = suspendCoroutine<Boolean> { continuation ->
            database.reference.updateChildren(postHashMap)
                .addOnSuccessListener {
                    Log.d("uploadPost", "모임 생성 성공")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    Log.d("uploadPost", "모임 생성 실패: $e")
                    continuation.resume(false)
                }
        }
        if (result) {
            val participantsListMap = hashMapOf<String, Any>(
                "/posts/$key/participants/${uid}" to true,
            )
            database.reference.updateChildren(participantsListMap)
                .addOnSuccessListener {
                    Log.d("uploadPost", "참여 목록 생성 성공")
                }.addOnFailureListener { e ->
                    Log.d("uploadPost", "참여 목록 생성 성공: $e")
                }
        }
    }

    suspend fun update(postData: Map<String, Any>, postID: String, participants: Map<String, Any>) {
        val postHashMap = hashMapOf<String, Any>(
            "/posts/$postID" to postData,
        )

        val updateResult = suspendCoroutine<Boolean> { continuation ->
            database.reference.updateChildren(postHashMap)
                .addOnSuccessListener {
                    Log.d("uploadPost", "모임 생성 성공")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    Log.d("uploadPost", "모임 생성 실패: $e")
                    continuation.resume(false)
                }
        }

        val participantsSetResult = suspendCoroutine<Boolean> { continuation ->
            val participantsListMap = hashMapOf<String, Any>(
                "/posts/$postID/participants" to participants,
            )
            database.reference.updateChildren(participantsListMap)
                .addOnSuccessListener {
                    Log.d("uploadPost", "참여 목록 생성 성공")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    Log.d("uploadPost", "참여 목록 생성 성공: $e")
                    continuation.resume(false)
                }
        }
    }

    suspend fun updateParticipationStatus(
        postID: String,
        action: Action
    ): Boolean {
        val result = suspendCoroutine<Boolean> { continuation ->
            if (action == Action.ADD) {
                database.reference.child("/posts/$postID/participants/$uid")
                    .setValue(true)
                    .addOnSuccessListener {
                        Log.d("updateParticipationStatus", "${uid}가 ${postID}에 참여 성공")
                        continuation.resume(true)
                    }
                    .addOnFailureListener {
                        Log.d("updateParticipationStatus", "${uid}가 ${postID}에 참여 실패")
                        continuation.resume(false)
                    }
            }
            if (action == Action.DELETE) {
                database.reference.child("/posts/$postID/participants/$uid").setValue(null)
                    .addOnSuccessListener {
                        Log.d("updateParticipationStatus", "${uid}가 ${postID}에 퇴장 성공")
                        continuation.resume(true)
                    }
                    .addOnFailureListener {
                        Log.d("updateParticipationStatus", "${uid}가 ${postID}에 퇴장 실패")
                        continuation.resume(false)
                    }
            }

        }
        return if (result) {
            Log.d("updateParticipationStatus", "$postID 참여 상태 업데이트 성공: $action")
            true
        } else {
            Log.d("updateParticipationStatus", "$postID 참여 상태 업데이트 실패: $action")
            false
        }
    }
}

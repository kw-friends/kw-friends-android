package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.authentication.UserAuth
import hello.kwfriends.ui.screens.home.HomeViewModel
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class PostDetail_(
    val gatheringTitle: String,
    val gatheringPromoterUID: String,
    val gatheringLocation: String,
    val gatheringTime: String,
    val maximumParticipants: String,
    val minimumParticipants: String,
    val gatheringDescription: String = "",
    val participantStatus: ParticipationStatus = ParticipationStatus.NOT_PARTICIPATED,
    val postID: String,
    val participants: Map<String, Boolean> = emptyMap()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "gatheringTitle" to gatheringTitle,
            "gatheringPromoter" to gatheringPromoterUID,
            "gatheringPromoterUID" to UserAuth.fa.uid.toString(),
            "gatheringLocation" to gatheringLocation,
            "gatheringTime" to gatheringTime,
            "maximumParticipants" to maximumParticipants,
            "minimumParticipants" to minimumParticipants,
            "gatheringDescription" to gatheringDescription
        )
    }
}

enum class ParticipationStatus {
    PARTICIPATED,
    NOT_PARTICIPATED,
    GETTING_IN,
    GETTING_OUT
}

object Post_ {

    private var database = Firebase.database.reference

    suspend fun initPostData(): MutableList<PostDetail_> {
        val posts = database.child("posts").get()
            .addOnSuccessListener {
                Log.d("initPostData", "모임 데이터 가져오기 성공 $it")
            }
            .addOnFailureListener { e ->
                Log.d("initPostData", "모임 데이터 가져오기 실패: $e")
            }.await()

        val postList = mutableListOf<PostDetail_>()
        for (postSnapshot in posts.children) {
            val postID = postSnapshot.key ?: continue
            val postDetail = postSnapshot.getValue(PostDetail_::class.java)
            postDetail?.let { postList.add(it) }
        }

        return postList
    }


   /* fun childEventListenerRecycler() {

        val context = this

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Map<String, Any>)
                Log.d("childEventListener.onChildAdded", "onChildAdded:" + snapshot.key!!)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("childEventListener.onChildChanged", "onChildChanged: ${snapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
//            val newComment = snapshot.getValue<Comment>()
                val postID = snapshot.key

                // ...
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("childEventListener.onChildRemoved", "onChildRemoved:" + snapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val postID = snapshot.key

                // ...
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(e: DatabaseError) {
                Log.d("childEventListener.onCancelled", "onCancelled: $e")
            }
        }
    }*/


    /*fun addPostEventListener(postReference: DatabaseReference) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = PostDetail_(
                    gatheringTitle = dataSnapshot.children("")
                )
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("postListener", "loadPost:onCancelled", databaseError.toException())
            }
        }

        postReference.addValueEventListener(postListener)
    }*/


    suspend fun upload(postData: Map<String, Any>) {
        val key = database.child("posts").push().key
        val postHashMap = hashMapOf<String, Any>(
            "/posts/$key" to postData,
        )

        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(postHashMap)
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
                "/posts/$key/participants/${Firebase.auth.currentUser!!.uid}" to true,
            )
            database.updateChildren(participantsListMap)
                .addOnSuccessListener {
                    Log.d("uploadPost", "참여 목록 생성 성공")
                }.addOnFailureListener { e ->
                    Log.d("uploadPost", "참여 목록 생성 성공: $e")
                }
        }
    }

    suspend fun updateParticipationStatus(target: String, viewModel: HomeViewModel) {

    }
}

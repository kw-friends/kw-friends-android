package hello.kwfriends.firebase.firestoreDatabase

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.authentication.UserAuth
import hello.kwfriends.firebase.realtimeDatabase.ParticipationStatus
import hello.kwfriends.ui.screens.auth.AuthViewModel
import hello.kwfriends.ui.screens.home.HomeViewModel
import hello.kwfriends.ui.screens.newPost.NewPostViewModel
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class PostDetail(
    val gatheringTitle: String,
    val gatheringPromoter: String,
    val gatheringLocation: String,
    val gatheringTime: String,
    val maximumParticipants: String,
    val minimumParticipants: String,
    val currentParticipants: String,
    val gatheringDescription: String,
    val participantStatus: ParticipationStatus,
    val postID: String
)

/*enum class ParticipationStatus {
    PARTICIPATED,
    NOT_PARTICIPATED,
    GETTING_IN,
    GETTING_OUT
}*/

object PostManager {
    val db = Firebase.firestore

    fun isDocumentExist(querySnapshot: QuerySnapshot, documentId: String): Boolean {
        return querySnapshot.documents.any { it.id == documentId }
    }

    suspend fun getPostDocuments(): QuerySnapshot? {
        val postsRes = suspendCoroutine<QuerySnapshot?> { continuation ->
            db.collection("posts").get()
                .addOnSuccessListener { documents ->
                    if (documents != null) {
                        Log.i("getDocRef", "게시글 가져옴: ${documents.documents}")
                        continuation.resume(documents)
                    } else {
                        Log.i("getDocRef", "게시글이 비어있어요.")
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("detDocRef", "게시글 불러오기 실패: ", exception)
                    continuation.resume(null)
                }
        }
        return postsRes
    }

    suspend fun getParticipantsDetail(documentSnapshot: DocumentSnapshot): QuerySnapshot {
        Log.w("Lim", "participants 불러오기: ${documentSnapshot}")
        val participantsDetail = suspendCoroutine<QuerySnapshot> { continuation ->
            db.collection("posts").document(documentSnapshot.id)
                .collection("participants").get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    Log.w("detDocRef", "participants 불러오기 실패: ", it)
                }
        }
        return participantsDetail
    }



    suspend fun uploadPost(
        gatheringTitle: String,
        gatheringPromoter: String,
        gatheringLocation: String,
        gatheringTime: String,
        maximumParticipants: String,
        minimumParticipants: String,
        gatheringDescription: String,
        newPostViewModel: NewPostViewModel
    ) {
        val post: HashMap<String, Any> = hashMapOf(
            "gatheringTitle" to gatheringTitle,
            "gatheringPromoter" to gatheringPromoter,
            "gatheringPromoterUID" to UserAuth.fa.uid.toString(),
            "gatheringLocation" to gatheringLocation,
            "gatheringTime" to gatheringTime,
            "maximumParticipants" to maximumParticipants,
            "minimumParticipants" to minimumParticipants,
            "gatheringDescription" to gatheringDescription
        )

        try {
            db.collection("posts")
                .add(post)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "모임 생성 성공. ID: ${documentReference.id}")
                    db.collection("posts").document(documentReference.id).collection("participants")
                        .document(UserAuth.fa.uid.toString())
                        .set(mapOf("name" to AuthViewModel.userInfo!!["name"].toString()))
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "하위 참가자 목록 컬렉션 생성 성공.")
                            newPostViewModel.showSnackbar("모임 생성 성공")
                            newPostViewModel.uploadResultUpdate(true)
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "모임 생성 실패: ", e)
                            newPostViewModel.showSnackbar("모임 생성 실패")
                            newPostViewModel.uploadResultUpdate(false)
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "모임 생성 실패: ", e)
                    newPostViewModel.showSnackbar("모임 생성 실패")
                    newPostViewModel.uploadResultUpdate(false)
                }
                .await()

        } catch (e: FirebaseFirestoreException) {
            Log.w(ContentValues.TAG, "모임 생성 실패")
            newPostViewModel.uploadResultUpdate(false)
        }
    }

    suspend fun updateParticipationState(target: String, viewModel: HomeViewModel) {
        val getStatus = db.collection("posts").document(target)
            .collection("participants").get()
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "모임 참여 여부 가져옴")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "모임 참여 여부 가져오는 중 문제 발생: ", e)
            }.await()

        if (getStatus.documents.any {it.id == UserAuth.fa.uid.toString()}) {
            db.collection("posts").document(target).collection("participants")
                .document(UserAuth.fa.uid.toString())
                .delete()
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "모임 참여 취소 성공")
                    viewModel.currentParticipationStatusMapUpdate(postID = target, add = -1)

                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "모임 참여 취소 실패: ", e)
                }.await()
        } else {
            db.collection("posts").document(target).collection("participants")
                .document(UserAuth.fa.uid.toString())
                .set(mapOf("name" to AuthViewModel.userInfo!!["name"].toString()))
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "모임 참여 성공")
                    viewModel.currentParticipationStatusMapUpdate(postID = target, add = 1)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "모임 참여 실패: ", e)
                }.await()
        }
    }

    suspend fun deletePost(target: String) {
        db.collection("posts").document(target)
            .delete()
            .addOnSuccessListener {
                Log.i("deletePost", "게시물 삭제 완료")
            }
            .addOnFailureListener { e ->
                Log.w("deletePost", "게시물 삭제 실패: ", e)
            }
            .await()
    }


}
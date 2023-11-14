package hello.kwfriends.firebase.firestoreManager

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.firebaseManager.UserAuth
import hello.kwfriends.ui.screens.auth.AuthViewModel
import hello.kwfriends.ui.screens.newPost.NewPostViewModel
import kotlinx.coroutines.tasks.await

data class PostDetail(
    val gatheringTitle: String,
    val gatheringPromoter: String,
    val gatheringLocation: String,
    val gatheringTime: String,
    val maximumParticipants: String,
    val minimumParticipants: String,
    val currentParticipants: String,
    val gatheringDescription: String,
    val postID: String
)

object PostManager {
    val db = Firebase.firestore

    suspend fun getPostRef(): List<PostDetail> {
        val postsRes = db.collection("posts").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.i("getDocRef", "게시글 가져옴: ${document.documents}")
                    for (document in document) {
                        Log.d("count", "${document.id} => ${document.data}")
                    }
                } else {
                    Log.i("getDocRef", "게시글이 비어있어요.")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("detDocRef", "게시글 불러오기 실패: ", exception)
            }.await()

        return postsRes.documents.map { document ->
            val participantsCount = db.collection("posts").document(document.id)
                .collection("participants").get()
                .await().size()

            PostDetail(
                gatheringTitle = document.getString("gatheringTitle") ?: "",
                gatheringPromoter = document.getString("gatheringPromoter") ?: "",
                gatheringLocation = document.getString("gatheringLocation") ?: "",
                gatheringTime = document.getString("gatheringTime") ?: "",
                maximumParticipants = document.getString("maximumParticipants") ?: "",
                minimumParticipants = document.getString("minimumParticipants") ?: "",
                currentParticipants = participantsCount.toString(),
                gatheringDescription = document.getString("gatheringDescription") ?: "",
                postID = document.id
            )
        }
    }


    suspend fun uploadPost(
        gatheringTitle: String,
        gatheringPromoter: String,
        gatheringLocation: String,
        gatheringTime: String,
        maximumParticipants: String,
        minimumParticipants: String,
        gatheringDescription: String,
        postViewModel: NewPostViewModel
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
                            postViewModel.showSnackbar("모임 생성 성공")
                            postViewModel.uploadResultUpdate(true)
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "모임 생성 실패: ", e)
                            postViewModel.showSnackbar("모임 생성 실패")
                            postViewModel.uploadResultUpdate(false)
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "모임 생성 실패: ", e)
                    postViewModel.showSnackbar("모임 생성 실패")
                    postViewModel.uploadResultUpdate(false)
                }
                .await()

        } catch (e: FirebaseFirestoreException) {
            Log.w(ContentValues.TAG, "모임 생성 실패")
            postViewModel.uploadResultUpdate(false)
        }
    }

    suspend fun updateParticipationState(target: String) {
        db.collection("posts").document(target).collection("participants")
            .document(AuthViewModel.userInfo!!["name"].toString())
            .set(mapOf("UID" to UserAuth.fa.uid))
            .addOnSuccessListener { documentReference ->
            }.await()
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
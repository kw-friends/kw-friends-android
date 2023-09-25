package hello.kwfriends.firebase.firestoreManager

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hello.kwfriends.ui.screens.post.NewPostViewModel
import kotlinx.coroutines.tasks.await

data class PostDetail(
    val gatheringTitle: String,
    val gatheringPromoter: String,
    val gatheringLocation: String,
    val gatheringTime: String,
    val maximumParticipant: Long,
)

object PostManager {
    val db = Firebase.firestore

    suspend fun getPostRef(): List<PostDetail> {
        val result = db.collection("posts")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.i("getDocRef", "게시글 가져옴: ${document.documents}") /////
                } else {
                    Log.i("getDocRef", "게시글이 비어있어요.")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("detDocRef", "게시글 불러오기 실패: ", exception)
            }.await()



        return result.documents.map { document ->
            PostDetail(
                gatheringTitle = document.getString("gatheringTitle") ?: "",
                gatheringPromoter = document.getString("gatheringPromoter") ?: "",
                gatheringLocation = document.getString("gatheringLocation") ?: "",
                gatheringTime = document.getString("gatheringTime") ?: "",
                maximumParticipant = document.getLong("maximumParticipant") ?: 0,
            )
        }
    }

    suspend fun uploadPost(
        gatheringTitle: String,
        gatheringPromoter: String,
        gatheringLocation: String,
        gatheringTime: String,
        maximumParticipant: String,
        postViewModel: NewPostViewModel
    ) {
        val post: HashMap<String, Any> = hashMapOf(
            "gatheringTitle" to gatheringTitle,
            "gatheringPromoter" to gatheringPromoter,
            "gatheringLocation" to gatheringLocation,
            "gatheringTime" to gatheringTime,
            "maximumParticipant" to maximumParticipant
        )
        try {
            db.collection("posts")
                .add(post)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "모임 생성 성공. ID: ${documentReference.id}")
                    postViewModel.showSnackbar("모임 생성 성공")
                    postViewModel.uploadResultUpdate(true)
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
package hello.kwfriends.firebase.firestoreManager

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    private fun postCertification(post: HashMap<String, Any>): Boolean {
        for ((key, value) in post) {
            when (key) {
                "gatheringTitle" -> {
                    if (value is String && value == "") {
                        Log.w("postCertification", "모임 제목이 없습니다.")
                        return false
                    }
                }

                "gatheringPromoter" -> {
                    if (value is String && value == "") {
                        Log.w("postCertification", "모임 주최자 정보가 없습니다")
                        return false
                    }
                }

                "gatheringLocation" -> {
                    if (value is String && value == "") {
                        Log.w("postCertification", "모임 위치 정보가 없습니다.")
                        return false

                    }
                }

                "gatheringTime" -> {
                    if (value is String && value == "") {
                        Log.w("postCertification", "모임 시간이 없습니다.")
                        return false
                    }
                }

                "maximumParticipant" -> {
                    if (value is Int && value <= 1) {
                        Log.w("postCertification", "최대 모임 인원은 2명 이상이어야 합니다")
                        return false
                    }
                }
            }
        }
        return true
    }

    suspend fun uploadPost(
        gatheringTitle: String,
        gatheringPromoter: String,
        gatheringLocation: String,
        gatheringTime: String,
        maximumParticipant: Int,
    ) {

        val post: HashMap<String, Any> = hashMapOf(
            "gatheringTitle" to gatheringTitle,
            "gatheringPromoter" to gatheringPromoter,
            "gatheringLocation" to gatheringLocation,
            "gatheringTime" to gatheringTime,
            "maximumParticipant" to maximumParticipant
        )

        if (postCertification(post)) {
            db.collection("posts")
                .add(post)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "게시글이 업로드 성공. ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "게시글 작성 실패: ", e)
                }
                .await()
        } else {
            Log.w("uploadPost", "게시물이 업로드되지 않았습니다.")
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
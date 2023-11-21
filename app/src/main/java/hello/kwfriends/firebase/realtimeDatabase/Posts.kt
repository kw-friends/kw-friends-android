package hello.kwfriends.firebase.realtimeDatabase

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.authentication.UserAuth
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class PostDetail_(
    val gatheringTitle: String,
    val gatheringPromoter: String,
    val gatheringLocation: String,
    val gatheringTime: String,
    val maximumParticipants: String,
    val minimumParticipants: String,
    val gatheringDescription: String,
    val participantStatus: ParticipationStatus,
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "gatheringTitle" to gatheringTitle,
            "gatheringPromoter" to gatheringPromoter,
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

    suspend fun upload(postData: Map<String, Any>): Boolean {
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
            return updateParticipationStatus(key ?: "")
        }
        return false
    }

    suspend fun updateParticipationStatus(key: String): Boolean {
        val participantsListMap = hashMapOf<String, Any>(
            "/posts/$key/participants/${Firebase.auth.currentUser!!.uid}" to true,
        )

        val result = suspendCoroutine<Boolean> { continuation ->
            database.updateChildren(participantsListMap)
                .addOnSuccessListener {
                    Log.d("uploadPost", "참여 목록 생성 성공")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    Log.d("uploadPost", "참여 목록 생성 성공: $e")
                    continuation.resume(false)
                }
        }
        return result
    }
}
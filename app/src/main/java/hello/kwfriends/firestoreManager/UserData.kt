package hello.kwfriends.firestoreManager

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object UserDataManager {

    private val fs = Firebase.firestore

    //유저 데이터 가져오기
    suspend fun getUserData(): Map<String, Any>?{
        Log.w("Lim", "user:${Firebase.auth.currentUser?.uid!!}")
        val result = suspendCoroutine<Map<String, Any>?> { continuation ->
            fs.collection("users")
                .document(Firebase.auth.currentUser?.uid!!)
                .get()
                .addOnSuccessListener { document ->
                    Log.w(ContentValues.TAG, "Firestore 유저 정보: ${document.data}")
                    continuation.resume(document.data)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "유저 정보를 불러오는데 실패했습니다.", e)
                    continuation.resumeWithException(e)
                }
        }
        return result
    }

    //유저 데이터 저장(없으면 생성, 있으면 merge)
    suspend fun mergeSetUserData(userInfo: Map<String, Any>): Boolean{
        val result = suspendCoroutine<Boolean> { continuation ->
            fs.collection("users").document(Firebase.auth.currentUser?.uid!!)
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    Log.w(ContentValues.TAG, "유저 정보를 firestore에 성공적으로 저장했습니다.")
                    continuation.resume(true)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "유저 정보를 firestore에 저장하는데 실패했습니다.", e)
                    continuation.resume(false)
                }
        }
        return result
    }
}
package hello.kwfriends.firebase.storage

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ProfileImage {
    val storage = Firebase.storage
    val profileImageRef = storage.reference.child("profiles")
    var myImageUri by mutableStateOf<Uri?>(null)

    //uri값을 받아 자신의 프로필 이미지로 firebase storage에 업로드
    suspend fun upload(uid: String, imageUri: Uri?): Boolean {
        if(imageUri == null){
            Log.w("Lim", "이미지 uri가 null이라 업로드에 실패했습니다.")
            return false
        }
        val result = suspendCoroutine<Boolean> { continuation ->
            val uploadRef = profileImageRef.child(uid)
            uploadRef.putFile(imageUri)
                .addOnProgressListener { send ->
                    val progress = (100.0 * send.bytesTransferred) / send.totalByteCount
                    Log.d("Lim", "Upload is $progress% done")
                }.addOnPausedListener {
                    Log.d("Lim", "Upload is paused")
                }
                .addOnFailureListener {
                    Log.d("Lim", "Upload is failed")
                    continuation.resume(false)
                }.addOnSuccessListener {
                    Log.d("Lim", "Upload is succeed")
                    continuation.resume(true)
                }
        }
        return result
    }

    //특정 uid의 프로필 이미지를 다운로드하여 uri를 반환
    suspend fun getDownloadUrl(uid: String): Uri? {
        val uidImageRef = storage.reference.child("profiles/${uid}")
        val result = suspendCoroutine<Uri?> { continuation ->
            uidImageRef.downloadUrl
                .addOnSuccessListener { uri ->
                continuation.resume(uri)
                }.addOnFailureListener {
                    continuation.resume(null)
                }
        }
        return result
    }

}
package hello.kwfriends.firebase.storageManager

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ProfileImage {
    val storage = Firebase.storage
    val profileImageRef = storage.reference.child("profiles/${Firebase.auth.currentUser!!.uid}")

    suspend fun upload(imageUri: Uri?): Boolean {
        if(imageUri == null){
            Log.w("Lim", "이미지 uri가 null이라 업로드에 실패했습니다.")
            return false
        }
        val result = suspendCoroutine<Boolean> { continuation ->
            profileImageRef.putFile(imageUri)
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

    suspend fun getDownloadUrl(uid: String): Uri? {
        val uidImageRef = storage.reference.child("profiles/${uid}")
        val result = suspendCoroutine<Uri?> { continuation ->
            uidImageRef.downloadUrl.addOnSuccessListener { uri ->
                // Got the download URL for 'users/me/profile.png'
                continuation.resume(uri)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
        return result
    }


}
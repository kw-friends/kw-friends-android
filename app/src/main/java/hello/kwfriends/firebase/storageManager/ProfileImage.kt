package hello.kwfriends.firebase.storageManager

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ProfileImage {
    private val imageFolderRef = Firebase.storage.reference.child("profiles/${Firebase.auth.currentUser!!.uid}")

    suspend fun upload(imageUri: Uri?): Boolean {
        if(imageUri == null){
            Log.w("Lim", "이미지 uri가 null이라 업로드에 실패했습니다.")
            return false
        }
        val result = suspendCoroutine<Boolean> { continuation ->
            imageFolderRef.putFile(imageUri)
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



}
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

object ChattingImage {
    val storage = Firebase.storage
    val chattingImageRef = storage.reference.child("chattingImage")
    var chattingUriMap by mutableStateOf<MutableMap<String, Uri?>>(mutableMapOf())

    fun updateChattingUriMap(imageID: String, uri: Uri?) {
        chattingUriMap = chattingUriMap.toMutableMap().apply {
            this[imageID] = uri
        }
    }

    //특정 uid의 프로필 이미지를 업로드
    suspend fun upload(imageID: String, imageUri: Uri?): Boolean {
        if (imageUri == null) {
            Log.w("ChattingImage.upload()", "이미지 uri가 null이라 업로드에 실패했습니다.")
            return false
        }
        val result = suspendCoroutine<Boolean> { continuation ->
            val uploadRef = chattingImageRef.child(imageID)
            uploadRef.putFile(imageUri)
                .addOnProgressListener { send ->
                    val progress = (100.0 * send.bytesTransferred) / send.totalByteCount
                    Log.d("ChattingImage.upload()", "Upload is $progress% done")
                }.addOnPausedListener {
                    Log.d("ChattingImage.upload()", "Upload is paused")
                }
                .addOnFailureListener {
                    Log.d("ChattingImage.upload()", "Upload is failed")
                    continuation.resume(false)
                }.addOnSuccessListener {
                    Log.d("ChattingImage.upload()", "Upload is succeed")
                    continuation.resume(true)
                }
        }
        return result
    }

    //특정 uid의 프로필 이미지를 다운로드하여 uri를 반환
    suspend fun getDownloadUrl(imageID: String): Uri? {
        Log.w("ChattingImage.getDownloadUrl", "${imageID} 이미지 불러오는 중")
        val uidImageRef = chattingImageRef.child(imageID)
        val result = suspendCoroutine<Uri?> { continuation ->
            uidImageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    continuation.resume(uri)
                    Log.w("ChattingImage.getDownloadUrl", "${imageID} 이미지 불러오기 성공")
                }.addOnFailureListener {
                    continuation.resume(null)
                    Log.w("ChattingImage.getDownloadUrl", "${imageID} Uri가져오기 실패")
                }
        }
        return result
    }
}
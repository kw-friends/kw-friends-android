package hello.kwfriends.firebase.storage

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object PostImage {
    val storage = Firebase.storage
    val postImageRef = storage.reference.child("postImage")
    var postUploadState: MutableMap<Uri?, Double> = mutableMapOf()
    val database = Firebase.database.reference
    var postUriMap by mutableStateOf(mutableMapOf<String, MutableMap<String, Uri>>()) // [PostID, [ImageID, Uri]]

    suspend fun uploadImage(postID: String, uriMap: List<Uri?>): MutableList<String> {
        val imageList: MutableList<String> = mutableListOf()

        for (uri in uriMap) {
            postUploadState[uri] = 0.0
        }

        val result = suspendCoroutine<Boolean> { continuation ->
            for (uri in uriMap) {
                uri?.let {
                    val key = database.child("posts/${postID}/images").push().key

                    if (key != null) {
                        imageList += key
                    }

                    postImageRef.child("${postID}/${key}").putFile(it)
                        .addOnProgressListener { send ->
                            val progress = (100.0 * send.bytesTransferred) / send.totalByteCount
                            postUploadState[uri] = progress
                            Log.d("PostImage.upload()", "Upload is $progress% done")
                        }.addOnPausedListener {
                            Log.d("PostImage.upload()", "Upload is paused")
                        }
                        .addOnFailureListener {
                            Log.d("PostImage.upload()", "Upload is failed")
                            continuation.resume(false)
                        }.addOnSuccessListener {
                            Log.d("PostImage.upload()", "Upload is succeed")
                            if (uri == uriMap.last()) {
                                continuation.resume(true)
                            }
                        }
                } ?: run {
                    Log.w("PostImage.uploadImage", "Uri is null")
                }
            }
        }

        return imageList
    }

    suspend fun getPostImageUri(postID: String, imageID: String) {
        Log.d("PostImage.getPostImageUri", "$imageID 이미지 요청")
        val postImageIDRef = postImageRef.child("$postID/$imageID")
        val uri = suspendCoroutine<Uri?> { continuation ->
            postImageIDRef.downloadUrl
                .addOnSuccessListener { uri ->
                    continuation.resume(uri)
                    Log.d("PostImage.getPostImageUri", "$imageID Uri 가져옴: $uri")

                    val updatedMap = postUriMap.toMutableMap()
                    val imagesMap = updatedMap.getOrPut(postID) { mutableMapOf() }
                    imagesMap[imageID] = uri
                    postUriMap =
                        updatedMap.toMap() as MutableMap<String, MutableMap<String, Uri>> // 상태 업데이트를 트리거
                }.addOnFailureListener {
                    continuation.resume(null)
                    Log.d("PostImage.getPostImageUri", "$imageID Uri 오류")
                }
        }
    }
}
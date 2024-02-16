package hello.kwfriends.firebase.storage

import android.net.Uri
import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object PostImage {
    val storage = Firebase.storage
    val postImageRef = storage.reference.child("postImage")
    val database = Firebase.database.reference

    suspend fun uploadImage(postID: String, uriMap: List<Uri?>): MutableList<String> {
        val imageList: MutableList<String> = mutableListOf()

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

    suspend fun getPostImageUri(postID: String, imageID: String): Uri? {
        Log.d("PostImage.getPostImageUri", "$imageID 이미지 요청")
        val postImageIDRef = postImageRef.child("$postID/$imageID")
        val uri = suspendCoroutine<Uri?> { continuation ->
            postImageIDRef.downloadUrl
                .addOnSuccessListener { uri ->
                    continuation.resume(uri)
                    Log.d("PostImage.getPostImageUri", "$imageID Uri 가져옴: $uri")
                }.addOnFailureListener {
                    continuation.resume(null)
                    Log.d("PostImage.getPostImageUri", "$imageID Uri 오류")
                }
        }
        return uri
    }
}
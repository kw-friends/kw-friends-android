package hello.kwfriends.firebase.storage

import android.net.Uri
import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object PostImage {
    val storage = Firebase.storage
    val postImageRef = storage.reference.child("postImage")
    val database = Firebase.database.reference

    suspend fun uploadImage(postID: String, uriMap: List<Uri?>): MutableList<String> {
        val imageKeys = mutableListOf<String>()

        val uploadJobs = uriMap.mapNotNull { uri ->
            uri?.let {
                coroutineScope {
                    async {
                        val key =
                            database.child("posts/$postID/images").push().key ?: return@async null
                        imageKeys.add(key)

                        try {
                            postImageRef.child("$postID/$key").putFile(uri)
                                .addOnSuccessListener { send ->
                                    val progress =
                                        (100.0 * send.bytesTransferred) / send.totalByteCount
                                    Log.d("PostImage.upload()", "Upload is $progress% done")
                                }
                                .addOnFailureListener{e ->
                                    Log.d("PostImage.upload()", "Upload is failed: $e")
                                }.await()
                            Log.d("UploadImage", "Upload for $key succeeded")
                            key // 성공시 키 반환
                        } catch (e: Exception) {
                            Log.e("UploadImage", "Upload for $key failed", e)
                            null // 실패시 null 반환
                        }
                    }
                }
            }
        }

        uploadJobs.awaitAll()

        return imageKeys
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
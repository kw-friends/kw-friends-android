package hello.kwfriends.firebase.CloudStorageManager

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.security.cert.CertPath

object ProfileImage {
    val imageFolderRef = Firebase.storage.reference.child("profiles")




    fun profileUpload(localPath: String) {
        var file = Uri.fromFile(File(localPath))
        var uploadTask = imageFolderRef.putFile(file)
            .addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...

            }
    }



}
package hello.kwfriends.ui.screens.profileImageScreen

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.storageManager.ProfileImage
import kotlinx.coroutines.launch

class ProfileImageViewModel : ViewModel() {

    var imageUri by mutableStateOf<Uri?>(null)

    fun imageUpload(){
        viewModelScope.launch {
            ProfileImage.upload(imageUri)
        }
    }

    fun imageLoad(){
        viewModelScope.launch {
            imageUri = ProfileImage.getDownloadUrl(Firebase.auth.currentUser!!.uid)
        }
    }

}
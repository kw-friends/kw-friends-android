package hello.kwfriends.ui.screens.profileImageScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hello.kwfriends.firebase.storageManager.ProfileImage
import kotlinx.coroutines.launch

class ProfileImageViewModel : ViewModel() {



    fun imageUpload(){
        viewModelScope.launch {
            ProfileImage.upload(ProfileImage.myImageUri)
        }
    }

    fun imageLoad(){
        viewModelScope.launch {
            ProfileImage.myImageUri = ProfileImage.getDownloadUrl(Firebase.auth.currentUser!!.uid)
        }
    }

}
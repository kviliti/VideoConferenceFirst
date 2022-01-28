package com.example.videoconference.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videoconference.utilities.Constants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {
    private val _usersList = MutableLiveData<List<DocumentSnapshot>>(emptyList())
    val listUsers: MutableLiveData<List<DocumentSnapshot>> = _usersList
    init {
        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    task.result!!.documents.forEach {
                        _usersList.value = task.result!!.documents }
                }
            }
    }
}
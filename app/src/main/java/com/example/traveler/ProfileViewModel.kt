package com.example.traveler

import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.example.traveler.data.Result
import com.example.traveler.data.User
import com.example.traveler.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val _userRepository: UserRepository

    private val _currentUser = MutableLiveData<User>()
    val currentUser : MutableLiveData<User> get() = _currentUser

    private val _favoriteCountries = MutableLiveData<List<Map<String, Any>>>()
    val favoriteCountries : MutableLiveData<List<Map<String, Any>>> get() = _favoriteCountries

    private val _journals = MutableLiveData<List<Map<String, Any>>>()
    val journals : MutableLiveData<List<Map<String, Any>>> get() = _journals

    private val _tasks = MutableLiveData<List<Map<String, Any>>>()
    val tasks : MutableLiveData<List<Map<String, Any>>> get() = _tasks

    init {
        _userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when(val result = _userRepository.getCurrentUser()){
                is Result.Success ->{
                    _currentUser.value = result.data
                }
                is Error -> {
                    //Error
                }

                else -> {}
            }
        }
    }

    fun loadFeaturesOfUser() {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            val firestore = Injection.instance()

            val collectionRef = user?.let {
                firestore.collection("users")
                    .document(it.uid).collection("favorites")
            }
            val snapshot = collectionRef?.get()?.await()
            val itemList = snapshot?.documents?.mapNotNull { document ->
                document.data?.toMutableMap()?.apply {
                    // including the document ID in the map to access along with its fields later
                    put("documentId", document.id)
                }
            }
            _favoriteCountries.value = itemList
        }
    }

    fun loadJournalsOfUser(user: User?, orderBy : String, list : MutableState<List<Journal>>){
        viewModelScope.launch {
            val firestore = Injection.instance()

            val collectionRef = user?.let {
                firestore.collection("users")
                    .document(it.uid).collection("journals").orderBy(orderBy)
            }
            val snapshot = collectionRef?.get()?.await()
//            var itemList = snapshot?.documents?.mapNotNull { document ->
//                document.data?.toMutableMap()?.apply {
//                    // including the document ID in the map to access along with its fields later
//                    put("documentId", document.id)
//                }
//            }
            var itemList = snapshot?.toObjects(Journal::class.java)
            if (itemList != null) {
                list.value = itemList
            }
        }
    }

    fun loadTasksOfUser(journal: Journal, dayNumber: Int) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            val firestore = Injection.instance()

            val collectionRef = user?.let {
                firestore.collection("users")
                    .document(it.uid).collection("journals").document(journal.title)
                    .collection("days").document("day$dayNumber")
                    .collection("tasks")
            }
            val snapshot = collectionRef?.get()?.await()
            val itemList = snapshot?.documents?.mapNotNull { document ->
                document.data?.toMutableMap()?.apply {
                    // including the document ID in the map to access along with its fields later
                    put("documentId", document.id)
                }
            }
            _tasks.value = itemList
        }
    }
}
package com.example.traveler

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.data.City
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.example.traveler.data.Result
import com.example.traveler.data.User
import com.example.traveler.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class ProfileViewModel : ViewModel() {

    private val _userRepository: UserRepository

    private val _currentUser = MutableLiveData<User>()
    val currentUser : MutableLiveData<User> get() = _currentUser

    private val _favoriteCountries = MutableLiveData<List<City>>()
    val favoriteCountries : MutableLiveData<List<City>> get() = _favoriteCountries

//    private val _journals = MutableLiveData<List<Map<String, Any>>>()
//    val journals : MutableLiveData<List<Map<String, Any>>> get() = _journals

    private val _tasks = MutableLiveData<List<Map<String, Any>>>()
    val tasks : MutableLiveData<List<Map<String, Any>>> get() = _tasks

    init {
        _userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
        loadCurrentUser()
    }

    fun loadCurrentUser() {
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

            val collectionRef = firestore.collection("users")
                    .document(user!!.uid).collection("favorites")

            val snapshot = collectionRef.get().await()
            val itemList = snapshot.toObjects<City>()

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
            var itemList = snapshot?.toObjects(Journal::class.java)
            if (itemList != null) {
                list.value = itemList
            }
        }
    }

//    fun loadTasksOfUser(journal: Journal, dayNumber: Int) {
//        viewModelScope.launch {
//            val user = FirebaseAuth.getInstance().currentUser
//            val firestore = Injection.instance()
//
//            val collectionRef = user?.let {
//                firestore.collection("users")
//                    .document(it.uid).collection("journals").document(journal.title)
//                    .collection("days").document("day$dayNumber")
//                    .collection("tasks")
//            }
//            val snapshot = collectionRef?.get()?.await()
//            val itemList = snapshot?.documents?.mapNotNull { document ->
//                document.data?.toMutableMap()?.apply {
//                    // including the document ID in the map to access along with its fields later
//                    put("documentId", document.id)
//                }
//            }
//            _tasks.value = itemList
//        }
//    }

    fun loadOngoingTrip(user: User, journal: MutableState<Journal>){
        viewModelScope.launch {
            try {
                val journalsCol = Injection.instance().collection("users")
                    .document(user.uid).collection("journals")

                val journalSnapshot = journalsCol.document(user.ongoing_trip).get().await()

                if (journalSnapshot.exists()) {
                    val journalData = journalSnapshot.toObject<Journal>()
                    journalData?.let {
                        journal.value = it
                    }
                } else {
                    // Handle case where document does not exist
                }
            } catch (e: Exception) {
                // Handle errors
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun isFollowing(user: User, isFollowing : MutableState<Boolean?>, followingBox : MutableState<String?>){

        GlobalScope.launch {
            isFollowing.value = false
            followingBox.value = "Follow"
            val firestore = Injection.instance()
            val currentUser = FirebaseAuth.getInstance().currentUser

            val followingCollectionSnapshot = currentUser?.let {
                firestore.collection("users").document(it.uid).collection("following")
                    .get().await()
            }

            followingCollectionSnapshot?.forEach { following ->
                if (user.uid == following.id) {
                    isFollowing.value = true
                    followingBox.value = "Following"
                }
            }

        }
    }

    @Composable
    fun UpdateJournal(journals : MutableState<List<Journal>>, onDismissRequest: () -> Unit,
                      onSuccessRequest: (Journal) -> Unit ){
        var title by remember {
            mutableStateOf("")
        }
        var location by remember {
            mutableStateOf("")
        }
        var color by remember {
            mutableStateOf(Color.White.value)
        }
        var startDateInMillis by remember {
            mutableStateOf(Calendar.getInstance().time.time)
        }
        var endDateInMillis by remember {
            mutableStateOf(Calendar.getInstance().time.time)
        }
        var startDate by remember {
            mutableStateOf("")
        }
        var endDate by remember {
            mutableStateOf("")
        }
        var notes by remember {
            mutableStateOf("")
        }

        Dialog(onDismissRequest = { onDismissRequest() }) {

            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 6.dp
            ) {

                LazyColumn(modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(0.75f),
                    verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally)
                {

                    journals.value.let {journals->

                        items(journals){journal->

                            Card(modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clickable {
                                    title = journal.title
                                    location = journal.location
                                    color = journal.color.toULong()
                                    startDateInMillis = journal.startDateInMillis
                                    endDateInMillis = journal.endDateInMillis
                                    startDate = journal.startDate
                                    endDate = journal.endDate
                                    notes = journal.notes

                                    val currentJournal = Journal(
                                        title = title,
                                        location = location,
                                        color = color.toString(),
                                        startDateInMillis = startDateInMillis,
                                        endDateInMillis = endDateInMillis,
                                        endDate = endDate,
                                        startDate = startDate,
                                        notes = notes
                                    )

                                    onSuccessRequest(currentJournal)
                                },
                                backgroundColor = Color(journal.color.toULong())
                            )
                            {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                                    Text(text = journal.title, modifier = Modifier.padding(10.dp))
                                }
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
                                    Text(text = journal.location, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
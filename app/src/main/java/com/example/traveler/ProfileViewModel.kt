package com.example.traveler

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
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

    private val _userRepository: UserRepository = UserRepository(
        FirebaseAuth.getInstance(),
        Injection.instance()
    )

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Injection.instance()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser : MutableLiveData<User?> get() = _currentUser

    private val _favoriteCities = MutableLiveData<List<City>>()
    val favoriteCities : MutableLiveData<List<City>> get() = _favoriteCities

    private val _followings = MutableLiveData<MutableList<User?>>()
    val followings : MutableLiveData<MutableList<User?>> get() = _followings

    private val _followers = MutableLiveData<MutableList<User?>>()
    val followers : MutableLiveData<MutableList<User?>> get() = _followers



    init {
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
            if(auth.uid != null){
                val favoritesCollectionRef = auth.uid?.let {
                    firestore.collection("users")
                        .document(it).collection("favorites")
                }

                val favoritesSnapshot = favoritesCollectionRef?.get()?.await()
                val itemList = favoritesSnapshot?.toObjects<City>()
                _favoriteCities.value = itemList
            }
        }
    }

    fun followingsOfUser(){
        viewModelScope.launch {
            if(auth.uid != null){
                val followingList = mutableListOf<User?>()

                val usersCollection = firestore.collection("users")

                val followingCollection = auth.uid?.let {
                    usersCollection.document(it)
                        .collection("following")
                }

                val followingsSnapshot = followingCollection?.get()?.await()
                followingsSnapshot?.documents?.forEach {document->

                    val snapshotUser = usersCollection.document(document.id).get().await()
                    followingList.add(snapshotUser.toObject(User::class.java))
                }
                _followings.value = followingList
            }
        }
    }

    fun followersOfUser(){
        viewModelScope.launch {
            if(auth.uid != null){
                val followersList = mutableListOf<User?>()

                val usersCollection = firestore.collection("users")

                val followersSnapshot = usersCollection.get().await()
                followersSnapshot.documents.forEach {document->
                    val documentsFollowingCol = document.reference.collection("following")
                    val documentsFollowingSnap = documentsFollowingCol.get().await()

                    documentsFollowingSnap.documents.forEach {possibleFollower->
                        if(possibleFollower.id == auth.uid){
                            val snapshotUser = usersCollection.document(document.id).get().await()
                            followersList.add(snapshotUser.toObject(User::class.java))
                        }
                    }
                }
                _followers.value = followersList
            }
        }
    }

    fun loadJournalsOfUser(user: User?, orderBy : String, list : MutableState<List<Journal>>){
        viewModelScope.launch {
            if(user != null && auth.uid != null){
                val collectionRef = user.let {
                    firestore.collection("users")
                        .document(it.uid).collection("journals").orderBy(orderBy)
                }
                val snapshot = collectionRef.get().await()
                val itemList = snapshot?.toObjects(Journal::class.java)
                if (itemList != null) {
                    list.value = itemList
                }
            }
        }
    }

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

            val followingCollectionSnapshot =
                auth.uid?.let {
                    firestore.collection("users").document(it).collection("following")
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
                      onSuccessRequest: (Journal) -> Unit )
    {
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
        var mostMemorableImage by remember {
            mutableStateOf("")
        }

        Dialog(onDismissRequest = { onDismissRequest() }) {

            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 6.dp
            ) {

                LazyColumn(modifier = Modifier
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
                                    mostMemorableImage = journal.mostMemorialImage

                                    val currentJournal = Journal(
                                        title = title,
                                        location = location,
                                        color = color.toString(),
                                        startDateInMillis = startDateInMillis,
                                        endDateInMillis = endDateInMillis,
                                        endDate = endDate,
                                        startDate = startDate,
                                        notes = notes,
                                        mostMemorialImage = mostMemorableImage
                                    )

                                    onSuccessRequest(currentJournal)
                                }
                            )
                            {

                                Image(painter = rememberAsyncImagePainter(model = journal.mostMemorialImage), contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds,
                                    alpha = 0.75f)

                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if(journal.mostMemorialImage.isEmpty()){
                                            Color(journal.color.toULong()).copy(0.75f)
                                        }
                                        else Color.Transparent
                                    ), contentAlignment = Alignment.TopStart) {
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

    @Composable
    fun Followings(
        followingsOrFollowers: MutableList<User?>,
        onClickProfile: (User) -> Unit,
        onDismissRequest: () -> Unit,
        onFollowBoxClick: (User, MutableState<String?>) -> Unit
    ){

        Dialog(onDismissRequest = { onDismissRequest() }) {

            Surface(
                modifier = Modifier.fillMaxHeight(0.5f),
                shape = RectangleShape,
                elevation = 6.dp
            ) {

                LazyColumn(modifier = Modifier
                    .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally)
                {

                    items(followingsOrFollowers){ user->
                        
                        val isFollowing by remember {
                            mutableStateOf(mutableStateOf<Boolean?>(null))
                        }

                        val followingBox by remember {
                            mutableStateOf(mutableStateOf<String?>(null))
                        }

                        LaunchedEffect(key1 = user){
                            isFollowing(user!!, isFollowing, followingBox)
                        }

                        Card(
                            border = BorderStroke(2.dp, Color.Black),
                            backgroundColor = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clickable {
                                    onClickProfile(user!!)
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart)
                            {
                                Row(modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp),
                                    verticalAlignment = Alignment.CenterVertically)
                                {
                                    Card(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .padding(3.dp),
                                        shape = CircleShape
                                    )
                                    {
                                        Image(painter = rememberAsyncImagePainter(model = user!!.profile_image),
                                            contentDescription = null, contentScale = ContentScale.Crop)
                                    }

                                    Column(modifier = Modifier.padding(3.dp)) {
                                        Text(text = user!!.fullName, fontSize = 18.sp)
                                        Text(text = if(user.about.length>=38){
                                            user.about.substring(0,38) + if(user.about.length>38) "..." else ""
                                            }
                                            else user.about,
                                            fontSize = 12.sp)
                                    }
                                }
                                Row(modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp)
                                    .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.End)
                                {
                                    Card(
                                        modifier = Modifier
                                            .padding(3.dp)
                                            .width(80.dp)
                                            .height(25.dp)
                                            .clickable {
                                                onFollowBoxClick(user!!, followingBox)
                                            }
                                    ) {
                                        Box(modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.LightGray),
                                            contentAlignment = Alignment.Center)
                                        {
                                            Text(text = followingBox.value.toString(), fontSize = 14.sp)
                                        }
                                    }
//                                Card(
//                                    modifier = Modifier
//                                        .padding(3.dp)
//                                        .width(40.dp)
//                                        .height(25.dp)
//                                ) {
//                                    Box(modifier = Modifier
//                                        .fillMaxSize()
//                                        .background(Color.LightGray),
//                                        contentAlignment = Alignment.Center)
//                                    {
//                                        Icon(painter = painterResource(id = R.drawable.baseline_message_24), contentDescription = null)
//                                    }
//                                }
                                }
                            }
                        }
                    }

                }
            }

        }
    }

    fun followAction(
        followingBox: MutableState<String?>,
        followed: User,
        followingNum: MutableState<Int?>
    ){

        if (followingBox.value == "Follow") {

            firestore
                .collection("users")
                .document(
                    currentUser.value!!.uid
                )
                .collection("following")
                .document(followed.uid).set(followed)
                .addOnSuccessListener {
                    firestore
                        .collection("users")
                        .document(currentUser.value!!.uid)
                        .update(
                            "following",
                            currentUser.value!!.following + 1
                        )
                    Injection
                        .instance()
                        .collection("users")
                        .document(followed.uid)
                        .update(
                            "followers",
                            followed.followers + 1
                        )
                    followingNum.value =
                        followingNum.value!! + 1
                    followed.followers++
                    println("User followed successfully")
                    currentUser.value!!.following++
                }
                .addOnFailureListener {
                    println("User couldn't followed. Error.")
                }
        followingBox.value = "Following"
        } else if (followingBox.value == "Following") {

            Injection
                .instance()
                .collection("users")
                .document(
                    currentUser.value!!.uid
                )
                .collection("following")
                .document(followed.uid)
                .delete()
                .addOnSuccessListener {
                    Injection
                        .instance()
                        .collection("users")
                        .document(currentUser.value!!.uid)
                        .update(
                            "following",
                            currentUser.value!!.following - 1
                        )
                    Injection
                        .instance()
                        .collection("users")
                        .document(followed.uid)
                        .update(
                            "followers",
                            followed.followers - 1
                        )
                    followingNum.value =
                        followingNum.value!! - 1
                    followed.followers--
                    println("User unfollowed successfully")
                    currentUser.value!!.following--
                }
                .addOnFailureListener {
                    println("User couldn't unfollowed. Error.")
                }
            followingBox.value = "Follow"
        }
    }
}

package com.example.traveler

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.example.traveler.data.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.util.Calendar
import java.util.UUID

@Composable
fun UserProfileScreen(
    navController: NavController, profileViewModel: ProfileViewModel,
    user: User? = profileViewModel.currentUser.value,
    journalPropertiesViewModel: JournalPropertiesViewModel,
    authenticationViewModel: AuthenticationViewModel
    ){

    val currentUser by profileViewModel.currentUser.observeAsState()

    val followings by profileViewModel.followings.observeAsState()

    val followers by profileViewModel.followers.observeAsState()

    val currentDate by remember {
        mutableStateOf(Calendar.getInstance().time.time)
    }

    val isOwnProfile by remember {
        mutableStateOf(user?.uid == currentUser?.uid)
    }

    val followingNum by remember {
        mutableStateOf(
            mutableStateOf(user?.following)
        )
    }

    var followingsExpanded by remember {
        mutableStateOf(false)
    }

    val followersNum by remember {
        mutableStateOf(
            mutableStateOf(user?.followers)
        )
    }

    var followersExpanded by remember {
        mutableStateOf(false)
    }

    val isFollowing by remember {
        mutableStateOf(mutableStateOf<Boolean?>(null))
    }

    val followingBox by remember {
        mutableStateOf(mutableStateOf<String?>(null))
    }

    LaunchedEffect(key1 = isOwnProfile){
        profileViewModel.loadCurrentUser()
    }

    LaunchedEffect(key1 = isFollowing){
        if (!isOwnProfile){
            if (user != null) {
                profileViewModel.isFollowing(user, isFollowing, followingBox)
            }
        }
    }

    var updateJournalExpanded by remember {
        mutableStateOf(false)
    }

    var ongoingJournal by remember {
        mutableStateOf(mutableStateOf(Journal()))
    }

    val journals by remember {
        mutableStateOf(mutableStateOf<List<Journal>>(emptyList()))
    }

    LaunchedEffect(key1 = true){
        profileViewModel.loadJournalsOfUser(user,"startDateInMillis",journals)
    }

    val context = LocalContext.current

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            Toast.makeText(context, "Please wait photo to be updated", Toast.LENGTH_SHORT).show()
            val storage = Firebase.storage
            val storageRef = storage.reference
            var imagesRef = storageRef.child("images/${UUID.randomUUID()}")
            val uploadImage = it?.let { it1 -> imagesRef.putFile(it1)
                .addOnCompleteListener {
                    println("Image uploaded successfully")
                }.addOnFailureListener{
                    println("Image couldn't uploaded")
                }
            }
            val urlTask = uploadImage?.continueWithTask{ task->
                if(!task.isSuccessful){
                    task.exception?.let {exception->
                        throw exception
                    }
                }else{
                    imagesRef.downloadUrl
                }
            }?.addOnCompleteListener { task->
                if(task.isSuccessful){
                    ongoingJournal.value = ongoingJournal.value.copy(mostMemorialImage = task.result.toString())
                    currentUser?.let { it1 ->
                        Injection.instance().collection("users").document(it1.uid)
                            .collection("journals").document(ongoingJournal.value.title)
                            .update("mostMemorialImage", ongoingJournal.value.mostMemorialImage)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Most memorial photo updated successfully", Toast.LENGTH_SHORT).show()
                            }
                    }
                    profileViewModel.loadJournalsOfUser(user,"endDateInMillis",journals)
                }else{
                    println("URL couldn't downloaded even though its uploaded")
                }
            }

        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.MainMenuScreen.route) }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }
            }, title = {}, backgroundColor = colorResource(id = R.color.app_bar_color),
                actions = {
                    IconButton(onClick = {
                        authenticationViewModel.signOut().also {
                            profileViewModel.currentUser.value = null
                            navController.navigate(Screen.LoginScreen.route)
                        }
                        authenticationViewModel.getGoogleSignInClient(context = context).signOut()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_logout_24), contentDescription = null)
                    }
                }
            )
        }
    ) {
        if(user != null && currentUser != null){
            followingNum.value = user.following
            followersNum.value = user.followers

            LazyColumn(
                modifier = Modifier
                    .padding(it),
                horizontalAlignment = Alignment.Start
            ) {

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 30.dp, vertical = 10.dp)
                                .padding(start = 10.dp)
                                .size(115.dp),
                            shape = CircleShape,
                            elevation = 20.dp
                        )
                        {
                            Image(
                                painter = rememberAsyncImagePainter(model = user.profile_image), //"https://cdn-icons-png.flaticon.com/512/3135/3135715.png"
                                contentDescription = null, contentScale = ContentScale.Crop
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center) {
                                Text(text = followingNum.value.toString(), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(70.dp))
                                Text(text = followersNum.value.toString(), fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 5.dp, horizontal = 10.dp)
                            ) {
                                Text(text = "Following", modifier = Modifier.clickable {
                                    followingsExpanded = true
                                })
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(text = "Followers", modifier = Modifier.clickable {
                                    followersExpanded = true
                                })
                            }

                            if(followingsExpanded){

                                LaunchedEffect(key1 = profileViewModel.followings){
                                    profileViewModel.followingsOfUser(user)
                                }

                                followings?.let { followings ->
                                    profileViewModel.Followings(
                                        followingsOrFollowers = followings,
                                        onClickProfile = { user->
                                            navController.currentBackStackEntry?.savedStateHandle?.set("user", user)
                                            navController.navigate(Screen.UserProfileScreen.route)
                                        },
                                        onDismissRequest = {
                                            followingsExpanded = false
                                        }
                                    ) { followed, followingBox ->
                                        val nothing = mutableStateOf<Int?>(null)
                                        profileViewModel.followAction(
                                            followingBox = followingBox,
                                            followed = followed,
                                            followingNum =
                                            if(isOwnProfile){
                                                followingNum
                                            }else nothing
                                        )
                                    }
                                }
                            } else if(followersExpanded){

                                LaunchedEffect(key1 = profileViewModel.followers){
                                    profileViewModel.followersOfUser(user)
                                }

                                followers?.let { followers ->
                                    profileViewModel.Followings(
                                        followingsOrFollowers = followers,
                                        onClickProfile = { user->
                                            navController.currentBackStackEntry?.savedStateHandle?.set("user", user)
                                            navController.navigate(Screen.UserProfileScreen.route)
                                        },
                                        onDismissRequest = {
                                            followersExpanded = false
                                        }
                                    ) { follower, followingBox ->
                                        val nothing = mutableStateOf<Int?>(null)
                                        profileViewModel.followAction(
                                            followingBox = followingBox,
                                            followed = follower,
                                            followingNum =
                                            if(isOwnProfile){
                                                followingNum
                                            }else nothing
                                        )
                                    }
                                }
                            }


                            if (!isOwnProfile) {
                                Row(
                                    horizontalArrangement = Arrangement.Center, modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                ) {
                                    Card(backgroundColor = Color.LightGray, modifier = Modifier
                                        .height(30.dp)
                                        .width(80.dp)
                                        .wrapContentSize()
                                        .clickable {
                                            profileViewModel.followAction(
                                                followingBox, user,
                                                followingNum = if (isOwnProfile) {
                                                    followingNum
                                                } else {
                                                    followersNum
                                                }
                                            )
                                        }
                                    ) {

                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {

                                            if (followingBox.value != null) {
                                                Text(
                                                    text = followingBox.value!!,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Row {
                        Text(
                            text = user.fullName, modifier = Modifier
                                .padding(end = 15.dp)
                                .padding(start = 60.dp),
                            fontWeight = FontWeight.Bold
                        )
                        if (isOwnProfile) {
                            Image(painter = painterResource(id = R.drawable.baseline_edit_24),
                                contentDescription = null,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { navController.navigate(Screen.EditProfileScreen.route) })
                        }
                    }
                    Text(
                        text = user.about,
                        modifier = Modifier
                            .padding(10.dp)
                            .padding(start = 19.dp),
                        maxLines = 3,
                    )

                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
                    {

                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(text = "Ongoing Trip", modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .alpha(0.7f), fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp, textAlign = TextAlign.Start
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Card(modifier = Modifier
                                .height(90.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp)
                                .clickable {
                                    if (isOwnProfile) {
                                        if (ongoingJournal.value.title.isNotEmpty()) {
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                "journal",
                                                ongoingJournal.value
                                            )
                                            navController.navigate(Screen.TripPlanTodaysPlanScreen.route)
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "You don't have ongoing trip right now, please" +
                                                            " update",
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        }
                                    }
                                }, shape = RoundedCornerShape(20.dp)
                            ) {
                                LaunchedEffect(key1 = ongoingJournal){
                                    profileViewModel.loadOngoingTrip(user, ongoingJournal)
                                }

                                Image(painter = rememberAsyncImagePainter(model = ongoingJournal.value.mostMemorialImage), contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds,
                                    alpha = 0.75f)

                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (ongoingJournal.value.mostMemorialImage.isEmpty()) Color(
                                            ongoingJournal.value.color.toULong()
                                        ).copy(0.75f)
                                        else Color.Transparent
                                    ), contentAlignment = Alignment.TopStart) {
                                    Text(text = ongoingJournal.value.title, modifier = Modifier.padding(10.dp))
                                }

                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
                                    Text(text = ongoingJournal.value.location, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                                }

                                if(isOwnProfile){
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                                        Icon(painter = painterResource(id = R.drawable.baseline_add_a_photo_24), contentDescription = null,
                                            tint = Color.LightGray, modifier = Modifier
                                                .clickable {
                                                    singlePhotoPicker.launch(
                                                        PickVisualMediaRequest(
                                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                                        )
                                                    )
                                                }
                                                .padding(10.dp))
                                    }
                                }

                            }
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(horizontal = 35.dp, vertical = 5.dp)
                                .fillMaxWidth()
                                .height(50.dp),
                            verticalAlignment = Alignment.Top) {
                            Text(text = "${journalPropertiesViewModel.getDayDifference(currentDate + 3 * 60 * 60 * 1000, ongoingJournal.value.endDateInMillis)} more days", modifier = Modifier
                                .alpha(0.6f),
                                fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            if(isOwnProfile){
                                Button(onClick = { updateJournalExpanded = true }, modifier = Modifier
                                    .height(30.dp)
                                    .width(135.dp)) {
                                    Text(text = "Add Update", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if(updateJournalExpanded){
                            profileViewModel.UpdateJournal(
                                journals = journals,
                                onDismissRequest = { updateJournalExpanded = false },
                                onSuccessRequest = {journal->
                                    ongoingJournal.value = journal
                                    Injection.instance().collection("users").document(user.uid)
                                        .update("ongoing_trip", journal.title).addOnSuccessListener {
                                            profileViewModel.currentUser.value!!.ongoing_trip = journal.title
                                            println("ongoing trip updated.")
                                        }.addOnFailureListener {
                                            println("ongoing trip couldn't updated.")
                                        }
                                    updateJournalExpanded = false
                                }
                            )
                        }

                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)) {
                            Text(text = "Recent Trips", modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .alpha(0.7f), fontSize = 20.sp,
                                fontWeight = FontWeight.Bold)
                        }

                        LaunchedEffect(key1 = true){
                            profileViewModel.loadJournalsOfUser(user,"endDateInMillis",journals)
                        }

                        LazyRow(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)) {

                            items(journals.value.reversed()) { journal ->

                                val journalEndDate = journal.endDateInMillis
                                if(
                                    currentDate + 3 * 60 * 60 * 1000 > journalEndDate
                                ){
                                    if(isOwnProfile || (!isOwnProfile && !journal.private))
                                        Column {
                                            Card(
                                                elevation = 20.dp,
                                                modifier = Modifier
                                                    .width(160.dp)
                                                    .height(140.dp)
                                                    .padding(8.dp)
                                                    .padding(horizontal = 8.dp)
                                                    .clickable {
                                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                                            "journal",
                                                            journal
                                                        )
                                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                                            "user",
                                                            user
                                                        )
                                                        navController.navigate(Screen.RecentTripScreen.route)
                                                    }
                                            ) {

                                                Image(painter = rememberAsyncImagePainter(model = journal.mostMemorialImage), contentDescription = null,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.FillBounds,
                                                    alpha = 0.75f)

                                                Box(modifier = Modifier
                                                    .background(
                                                        if (journal.mostMemorialImage.isEmpty()) {
                                                            Color(journal.color.toULong()).copy(
                                                                0.75f
                                                            )
                                                        } else Color.Transparent
                                                    )
                                                    .fillMaxSize()
                                                    .padding(8.dp)
                                                    .background(Color.Transparent),
                                                    contentAlignment = Alignment.BottomStart){
                                                    Text(text = journal.location, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                                }
                                            }
                                            Text(text = "${journalPropertiesViewModel.getDayDifference(journalEndDate,currentDate + 3 * 60 * 60 * 1000)} days ago...", fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .alpha(0.5f)
                                                    .padding(horizontal = 15.dp))
                                        }
                                }
                            }

                        }

                        if(isOwnProfile){
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Bottom, modifier = Modifier
                                .fillMaxSize()
                                .padding(25.dp)) {
                                Image(painter = painterResource(id = R.drawable.baseline_add_circle_outline_24), contentDescription = null,
                                    alignment = Alignment.BottomEnd, modifier = Modifier
                                        .alpha(0.7f)
                                        .size(70.dp)
                                        .fillMaxWidth()
                                        .padding(end = 20.dp)
                                        .padding(bottom = 10.dp)
                                        .clickable {
                                            navController.navigate(Screen.AddJournalScreen.route)
                                        }
                                )
                                Text(text = "Add Journal", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                    textAlign = TextAlign.End)
                            }
                        }
                    }
                }
            }
        }else{
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
            }
        }
    }
    
}

@Preview(showBackground = true)
@Composable
fun prrreview(journalPropertiesViewModel: JournalPropertiesViewModel = viewModel()){

}
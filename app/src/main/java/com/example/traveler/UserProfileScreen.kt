package com.example.traveler

import android.widget.Toast
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
import androidx.compose.material.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.example.traveler.data.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import java.util.Calendar

@Composable
fun UserProfileScreen(
    navController: NavController, profileViewModel: ProfileViewModel = viewModel(),
    user: User? = profileViewModel.currentUser.value,
    journalPropertiesViewModel: JournalPropertiesViewModel = viewModel()
    ){

    val currentUser by profileViewModel.currentUser.observeAsState()

    val isOwnProfile by remember {
        mutableStateOf(user?.uid == currentUser?.uid)
    }

    var followingNum by remember {
        mutableStateOf(
            user?.following
        )
    }

    var followersNum by remember {
        mutableStateOf(
            user?.followers
        )
    }

    val isFollowing by remember {
        mutableStateOf(mutableStateOf<Boolean?>(null))
    }

    val followingBox by remember {
        mutableStateOf(mutableStateOf<String?>(null))
    }

    LaunchedEffect(key1 = isFollowing){
        if (!isOwnProfile){
            if (user != null) {
                profileViewModel.isFollowing(user, isFollowing, followingBox)
            }
        }
    }

    var expanded by remember {
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
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Screen.LoginScreen.route)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_logout_24), contentDescription = null)
                    }
                }
            )
        }
    ) {
        if(user != null && currentUser != null){
            followingNum = user.following
            followersNum = user.followers

            LazyColumn(
                modifier = Modifier
                    .padding(it),
                horizontalAlignment = Alignment.Start
            ) {

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
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
                                .fillMaxWidth(), horizontalAlignment = Alignment.End
                        ) {

                            Row(modifier = Modifier.padding(end = 70.dp)) {
                                Text(text = followingNum.toString(), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(70.dp))
                                Text(text = followersNum.toString(), fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 5.dp, horizontal = 10.dp)
                                    .padding(end = 30.dp)
                            ) {
                                Text(text = "Following")
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(text = "Followers")
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
                                        .clickable {
                                            if (followingBox.value == "Follow") {

                                                Injection
                                                    .instance()
                                                    .collection("users")
                                                    .document(
                                                        currentUser!!.uid
                                                    )
                                                    .collection("following")
                                                    .document(user.uid)
                                                    .set(user)
                                                    .addOnSuccessListener {
                                                        Injection
                                                            .instance()
                                                            .collection("users")
                                                            .document(currentUser!!.uid)
                                                            .update(
                                                                "following",
                                                                currentUser!!.following + 1
                                                            )
                                                        Injection
                                                            .instance()
                                                            .collection("users")
                                                            .document(user.uid)
                                                            .update(
                                                                "followers",
                                                                user.followers + 1
                                                            )
                                                        followersNum = followersNum!! + 1
                                                        user.followers++
                                                        println("User followed successfully")
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
                                                        currentUser!!.uid
                                                    )
                                                    .collection("following")
                                                    .document(user.uid)
                                                    .delete()
                                                    .addOnSuccessListener {
                                                        Injection
                                                            .instance()
                                                            .collection("users")
                                                            .document(currentUser!!.uid)
                                                            .update(
                                                                "following",
                                                                currentUser!!.following - 1
                                                            )
                                                        Injection
                                                            .instance()
                                                            .collection("users")
                                                            .document(user.uid)
                                                            .update(
                                                                "followers",
                                                                user.followers - 1
                                                            )
                                                        followersNum = followersNum!! - 1
                                                        user.followers--
                                                        println("User unfollowed successfully")
                                                    }
                                                    .addOnFailureListener {
                                                        println("User couldn't unfollowed. Error.")
                                                    }
                                                followingBox.value = "Follow"
                                            }
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
                                alpha = 0.5f,
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
                                fontSize = 20.sp, textAlign = TextAlign.Start
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
                                },backgroundColor = Color(ongoingJournal.value.color.toULong()), shape = RoundedCornerShape(20.dp)
                            ) {
                                LaunchedEffect(key1 = ongoingJournal){
                                    profileViewModel.loadOngoingTrip(user, ongoingJournal)
                                }

                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                                    Text(text = ongoingJournal.value.title, modifier = Modifier.padding(10.dp))
                                }
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
                                    Text(text = ongoingJournal.value.location, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(horizontal = 35.dp, vertical = 5.dp)
                                .fillMaxWidth()
                                .height(50.dp),
                            verticalAlignment = Alignment.Top) {
                            Text(text = "2 more days", modifier = Modifier
                                .alpha(0.6f),
                                fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            if(isOwnProfile){
                                Spacer(modifier = Modifier.width(140.dp))
                                Button(onClick = { expanded = true }, modifier = Modifier
                                    .height(30.dp)
                                    .width(135.dp)) {
                                    Text(text = "Add Update", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if(expanded){
                            profileViewModel.UpdateJournal(
                                journals = journals,
                                onDismissRequest = { expanded = false },
                                onSuccessRequest = {journal->
                                    ongoingJournal.value = journal
                                    Injection.instance().collection("users").document(user.uid)
                                        .update("ongoing_trip", journal.title).addOnSuccessListener {
                                            println("ongoing trip updated.")
                                        }.addOnFailureListener {
                                            println("ongoing trip couldn't updated.")
                                        }
                                    expanded = false
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

                                val currentDate = Calendar.getInstance().time.time
                                val journalEndDate = journal.endDateInMillis
                                if(
                                    currentDate>journalEndDate
                                ){
                                    if(isOwnProfile || (!isOwnProfile && !journal.private))
                                        Column {
                                            Card(
                                                elevation = 20.dp,
                                                backgroundColor = Color(journal.color.toULong()),
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
                                                Box(modifier = Modifier.fillMaxSize()) {

                                                    Box(modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(8.dp)
                                                        .background(Color.Transparent),
                                                        contentAlignment = Alignment.BottomStart){
                                                        Text(text = journal.location.toString(), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                                                    }
                                                }
                                            }
                                            Text(text = "${journalPropertiesViewModel.getDayDifference(journalEndDate,currentDate)} days ago...", fontWeight = FontWeight.Bold,
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
                                        .padding(end = 20.dp).padding(bottom = 10.dp)
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

@Preview
@Composable
fun prrreview(){
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

                }
        )
        Text(text = "Add Journal", fontWeight = FontWeight.Bold, fontSize = 16.sp,
            textAlign = TextAlign.End)
    }
}
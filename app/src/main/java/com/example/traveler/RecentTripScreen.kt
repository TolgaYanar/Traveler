package com.example.traveler

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.example.traveler.data.Notes
import com.example.traveler.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions

@Composable
fun RecentTripScreen(navController: NavController, journal: Journal, user: User,
                     journalPropertiesViewModel: JournalPropertiesViewModel = viewModel()){

    val notes by remember {
        mutableStateOf(mutableStateOf(emptyList<Notes>()))
    }

    var private by remember {
        mutableStateOf(journal.private)
    }

    LaunchedEffect(key1 = true){
        journalPropertiesViewModel.getNotes(journal, notes, user = user)
    }

    Box(modifier = Modifier.fillMaxSize()){
        AsyncImage(modifier = Modifier.fillMaxSize(),model = journal.mostMemorialImage, contentDescription = null,
            contentScale = ContentScale.Crop)

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Row(modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
                if(private){
                    IconButton(onClick = {
                        journal.private = false
                        private = false
                        FirebaseAuth.getInstance().uid?.let {
                            Injection.instance().collection("users").document(it)
                                .collection("journals").document(journal.title).set(journal, SetOptions.merge())
                        }
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    }
                }
                else{
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
            Column {
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f))
                Surface(
                    contentColor = Color.Black,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(25.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){

                        item {

                            Row(modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start) {
                                Card(backgroundColor = Color.Magenta, modifier = Modifier.padding(4.dp),
                                    shape = RoundedCornerShape(15.dp)){
                                    Text(text = "${journalPropertiesViewModel.getDayDifference(journal.startDateInMillis,journal.endDateInMillis)} days",
                                        fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                                }
                                Card(backgroundColor = Color.Magenta, modifier = Modifier.padding(4.dp),
                                    shape = RoundedCornerShape(15.dp)
                                ){
                                    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Place, contentDescription = null)
                                        Text(text = journal.location,
                                            fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = journal.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                        items(notes.value) {
                            if(it.note.startsWith("https://firebasestorage.googleapis.com/v0/b/traveller-4d4df.appspot.com/o/images")){
                                Card(
                                    modifier = Modifier
                                        .height(250.dp)
                                        .width(250.dp)
                                        .padding(20.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                        Image(painter = rememberAsyncImagePainter(model = it.note,
                                            contentScale = ContentScale.Crop), contentDescription = null,
                                            contentScale = ContentScale.Crop)
                                    }
                                }
                            }
                            else{
                                Text(
                                    text = it.note,
                                    modifier = Modifier
                                        .width(325.dp)
                                        .padding(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}




@Preview
@Composable
fun preeeview(){
    RecentTripScreen(navController = rememberNavController(), journal = Journal(), User())
}
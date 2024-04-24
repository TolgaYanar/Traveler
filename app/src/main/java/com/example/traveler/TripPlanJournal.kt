package com.example.traveler

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.example.traveler.data.Notes
import com.example.traveler.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlanJournalScreen(navController: NavController, journal: Journal,
                          profileViewModel: ProfileViewModel,
                          journalPropertiesViewModel: JournalPropertiesViewModel){

    val user by profileViewModel.currentUser.observeAsState()

    val notes by remember {
        mutableStateOf(mutableStateOf(emptyList<Notes>()))
    }

    var deleteMode by remember {
        mutableStateOf(false)
    }

    val deleteFolder by remember {
        mutableStateOf(mutableListOf<Notes>())
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Trip Plan") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.UserProfileScreen.route)
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("journal", journal)
                        navController.navigate(Screen.AddNotesScreen.route)
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                    }

                    IconButton(onClick = {
                        deleteMode = !deleteMode
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), verticalArrangement = Arrangement.Top
        )
        {
            if(user != null){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceEvenly
                )
                {
                    Card(
                        modifier = Modifier
                            .height(45.dp)
                            .width(160.dp)
                            .clickable {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "journal",
                                    journal
                                )
                                navController.navigate(Screen.TripPlanTodaysPlanScreen.route)
                            },
                        backgroundColor = Color.Gray.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(25.dp),
                    ) {
                        Text(text = "Today's Plan", textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 10.dp), fontSize = 16.sp,
                            fontWeight = FontWeight.Bold)
                    }

                    Card(
                        modifier = Modifier
                            .height(45.dp)
                            .width(160.dp),
                        backgroundColor = Color(0xFF728FF3),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(text = "Journal", textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 10.dp), fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                LaunchedEffect(key1 = true){
                    journalPropertiesViewModel.getNotes(journal, notes, user)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {

                    items(notes.value) {note->

                        Row{
                            if (deleteMode){
                                var checked by remember {
                                    mutableStateOf(false)
                                }

                                Column(verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.height(100.dp))
                                {
                                    Checkbox(checked = checked, onCheckedChange = {
                                        checked = !checked
                                        if(checked){
                                            deleteFolder.add(note)
                                        }else{
                                            deleteFolder.remove(note)
                                        }
                                    })
                                }
                            }

                            if(note.note.startsWith("https://firebasestorage.googleapis.com/v0/b/traveller-4d4df.appspot.com/o/images")){
                                Card(
                                    modifier = Modifier
                                        .height(250.dp)
                                        .width(250.dp)
                                        .padding(20.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                        Image(painter = rememberAsyncImagePainter(model = note.note,
                                            contentScale = ContentScale.Crop), contentDescription = null,
                                            contentScale = ContentScale.Crop)
                                    }
                                }
                            }
                            else{
                                Text(text = note.note, modifier = Modifier
                                    .width(325.dp)
                                    .padding(20.dp))
                            }
                        }
                    }

                    if(deleteMode){
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(onClick = {
                                    deleteFolder.forEach {
                                        journalPropertiesViewModel.deleteNote(journal, it, user)
                                    }
                                    navController.currentBackStackEntry?.savedStateHandle?.set("journal", journal)
                                    navController.navigate(Screen.TripPlanJournalScreen.route)
                                }) {
                                    Text(text = "Delete", fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }else{
                CircularProgressIndicator()
            }
        }
    }

}
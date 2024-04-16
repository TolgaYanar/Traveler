package com.example.traveler

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material3.TextField
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.storage
import java.util.Calendar
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotesScreen(navController: NavController, journal: Journal){

    val context = LocalContext.current

    var textSelected by remember {
        mutableStateOf(false)
    }
    var imageSelected by remember {
        mutableStateOf(false)
    }

    var text by remember {
        mutableStateOf("")
    }
    var image by remember {
        mutableStateOf("")
    }

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
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
                   image = task.result.toString()
                }else{
                    println("URL couldn't downloaded even though its uploaded")
                }
            }

        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Add Notes") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.TripPlanJournalScreen.route) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF728FF3)
                ))
        }
    ) {

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally)
        {

            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 20.dp)
                ) {
                    Card(
                        backgroundColor = if(textSelected) Color.Blue else Color.Gray,
                        modifier = Modifier
                            .height(40.dp)
                            .width(100.dp)
                            .clickable {
                                imageSelected = false
                                textSelected = true
                            },
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                            Icon(painter = painterResource(id = R.drawable.baseline_text_fields_24), contentDescription = null)
                        }
                    }
                    Card(
                        backgroundColor = if(imageSelected) Color.Blue else Color.Gray,
                        modifier = Modifier
                            .height(40.dp)
                            .width(100.dp)
                            .clickable {
                                textSelected = false
                                imageSelected = true
                            },
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                            Icon(painter = painterResource(id = R.drawable.baseline_photo_24), contentDescription = null)
                        }
                    }
                }

                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp))
            }

            item {
                if(imageSelected){
                    Card(
                        backgroundColor = Color.Gray,
                        modifier = Modifier
                            .padding(vertical = 30.dp)
                            .height(250.dp)
                            .width(250.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(painter = rememberAsyncImagePainter(model = image,
                                contentScale = ContentScale.Crop), contentDescription = null,
                                contentScale = ContentScale.Crop)
                        }
                    }

                    Button(onClick = {
                        singlePhotoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    })
                    {
                        Text(text = "Select Image")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(30.dp), horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Button(onClick = {
                            uploadToNotes(image, journal, context)
                        }) {
                            Text(text = "Add Image")
                        }
                    }
                }
                else if (textSelected){

                    TextField(value = text, onValueChange = {text = it},
                        modifier = Modifier
                            .padding(20.dp)
                            .height(400.dp)
                            .width(300.dp), colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFE8E8E8),
                        focusedContainerColor = Color(0xFFE8E8E8)
                    ),
                        shape = RoundedCornerShape(25.dp),
                        label = {
                            Text(text = "Notes")
                        }
                    )

                    Button(onClick = {
                        uploadToNotes(text, journal, context)
                        text = ""
                    }) {
                        Text(text = "Add Text")
                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun addnotespreview(){
    AddNotesScreen(navController = rememberNavController(), journal = Journal())
}

fun uploadToNotes(imageOrText : String, journal : Journal, context: Context){
    val user = FirebaseAuth.getInstance().currentUser
    val added = Calendar.getInstance().time.time
    val firestore = Injection.instance()
    val notesCollection = firestore.collection("users").document(user!!.uid).collection("journals")
        .document(journal.title).collection("notes")
    val noteDoc = notesCollection.document(added.toString())
    val hashmap = hashMapOf<String, Any>(
        "note" to imageOrText,
        "added" to added
    )
    noteDoc.set(hashmap).addOnSuccessListener {
        Toast.makeText(context, "Added to notes successfully", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener {
        Toast.makeText(context, "Couldn't added to notes. Error.", Toast.LENGTH_SHORT).show()
    }
}
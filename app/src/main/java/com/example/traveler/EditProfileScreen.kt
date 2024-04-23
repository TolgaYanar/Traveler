package com.example.traveler

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traveler.data.Injection
import com.example.traveler.data.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun EditProfileScreen(navController: NavController,
                      authenticationViewModel: AuthenticationViewModel,
                      profileViewModel: ProfileViewModel){

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var user by remember { mutableStateOf<User?>(null) }
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
                    Toast.makeText(context, "Photo updated successfully", Toast.LENGTH_SHORT).show()
                    imagesRef.downloadUrl
                }
            }?.addOnCompleteListener { task->
                if(task.isSuccessful){
                    user?.profile_image = task.result.toString()
                }else{
                    println("URL couldn't downloaded even though its uploaded")
                }
            }

        }
    )

    var newEmail by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    // Retrieve user data from Firestore
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val firestore = FirebaseFirestore.getInstance()
            val userDocument = firestore.collection("users").document(currentUser.uid)
            try {
                val documentSnapshot = userDocument.get().await()
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject(User::class.java)
                    userData?.let {
                        user = it
                    }
                } else {
                    // User document does not exist
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    Scaffold(
        topBar = { AppBarView(title = "Edit Profile", arrow = true,
            onBackNavClicked = { navController.navigate(Screen.UserProfileScreen.route) }) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color(0XFFADD8E6)),
            horizontalAlignment = Alignment.Start
        ){
            item {

                if(user != null){
                    Row(modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp), horizontalArrangement = Arrangement.Center) {
                        Card(modifier = Modifier
                            .size(100.dp),
                            backgroundColor = Color.LightGray,
                            shape = CircleShape) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(painter = rememberAsyncImagePainter(model = user!!.profile_image, contentScale = ContentScale.Crop), contentDescription = null,
                                    contentScale = ContentScale.Crop)
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(text = "Edit Profile Picture", color = Color.Black, modifier = Modifier
                            .alpha(0.7f)
                            .clickable {
                                singlePhotoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            })
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Column(modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){


                        TextField(value = user!!.fullName, onValueChange = {fullname -> user = user!!.copy(fullName = fullname)},
                            trailingIcon = {
                                Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = null,
                                    tint = Color.Black)
                            },
                            singleLine = true, colors = TextFieldDefaults
                                .colors(unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black),
                            modifier = Modifier.width(330.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        TextField(value = newEmail, onValueChange = {email -> newEmail = email},
                            trailingIcon = {
                                Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = null,
                                    tint = Color.Black)
                            },
                            singleLine = true, colors = TextFieldDefaults
                                .colors(unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black),
                            modifier = Modifier.width(330.dp),
                            placeholder = {
                                if (currentUser != null) {
                                    currentUser.email?.let { it1 -> Text(text = it1, color = Color.Black) }
                                }
                            }
                        )


                        Spacer(modifier = Modifier.height(20.dp))

                        TextField(value = currentPassword, onValueChange = {currentPassword = it},
                            trailingIcon = {
                                Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = null,
                                    tint = Color.Black)
                            },
                            singleLine = true, colors = TextFieldDefaults
                                .colors(unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black),
                            label = {
                                Text(text = "Current Password", color = Color.Black)
                            }, modifier = Modifier.width(330.dp))

                        Spacer(modifier = Modifier.height(20.dp))

                        TextField(value = newPassword, onValueChange = {newPassword = it},
                            trailingIcon = {
                                Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = null,
                                    tint = Color.Black)
                            },
                            singleLine = true, colors = TextFieldDefaults
                                .colors(unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    ),
                            label = {
                                Text(text = "New Password", color = Color.Black)
                            }, modifier = Modifier.width(330.dp))

                        Spacer(modifier = Modifier.height(20.dp))


                        TextField(value = user!!.about, onValueChange = {about -> user = user!!.copy(about = about)},
                            trailingIcon = {
                                Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = null,
                                    tint = Color.Black)
                            },
                            singleLine = false, maxLines = 3, colors = TextFieldDefaults
                                .colors(unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black),
                            modifier = Modifier.width(330.dp),
                        )

                    }
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.End) {
                        Button(onClick = {
                            if (user != null && currentUser != null) {
                                if((newEmail != currentUser.email && newEmail.isNotEmpty()) && newPassword.isNotEmpty()){
                                    Toast.makeText(context, "Email and password cannot be updated at the same time.", Toast.LENGTH_LONG).show()
                                    navController.navigate(Screen.EditProfileScreen.route)
                                }
                                else{

                                    Injection.instance().collection("users").document(user!!.uid).set(user!!)
                                    profileViewModel.currentUser.value = user

                                    if(newPassword.isNotEmpty() && currentPassword.isNotEmpty()) {
                                        authenticationViewModel.reauthentication(
                                            user = currentUser,
                                            currentPassword = currentPassword,
                                            newPassword = newPassword,
                                            context = context
                                        )
                                    }
                                    else if(newEmail != currentUser.email && newEmail.isNotEmpty()){
                                        authenticationViewModel.updateEmail(newEmail, context)
                                    }
                                    navController.navigate(Screen.UserProfileScreen.route)
                                }
                            }
                        }, modifier = Modifier
                            .padding(40.dp)
                            .width(125.dp)) {
                            Text(text = "SAVE", fontWeight = FontWeight.Bold)
                        }
                    }
                }else{
                    CircularProgressIndicator(modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center))
                }
            }
        }
    }
}

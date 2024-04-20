package com.example.traveler

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.traveler.data.Injection
import com.example.traveler.ui.theme.TravelerTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val imeState = rememberImeState()
            val scrollState = rememberScrollState()

            LaunchedEffect(key1 = imeState.value) {
                if (imeState.value){
                    scrollState.animateScrollTo(scrollState.maxValue, tween(300))
                }
            }
            TravelerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(context = context)

                }
            }
            FirebaseAuth.getInstance().addIdTokenListener(com.google.firebase.auth.FirebaseAuth.IdTokenListener { auth ->
                val user = auth.currentUser
                if (user != null && user.isEmailVerified) {
                    // User's email is verified and updated
                    val newEmail = user.email
                    Injection.instance().collection("users").document(user.uid).update("email", newEmail)
                        .addOnSuccessListener {
                            println("User's email is verified and updated: ${newEmail}")
                        }.addOnFailureListener {
                            println("User's email is verified but couldn't updated: ${newEmail}")
                        }
                }
            })
        }
    }
}


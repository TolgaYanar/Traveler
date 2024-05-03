package com.example.traveler.data

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.traveler.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    //for google sign in
    val clientID = "242763116994-4ppf50d829la5nv1otsq5snk1v2qi444.apps.googleusercontent.com"

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String
    ) : Result<Boolean> =
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            //Add user to firestore
            val user = User(fullName, email)
            saveUserToFirestore(user = user)
            Result.Success(true)
        }catch (e: Exception){
            Result.Fail(e)
        }

    suspend fun signIn(
        email: String,
        password: String
    ) : Result<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(true)
        }catch (e: Exception){
            Result.Fail(e)
        }

    private suspend fun saveUserToFirestore(user: User){
        val userId = auth.currentUser?.uid
        if (userId != null) {
            user.uid = userId
        }
        if (userId != null) {
            firestore.collection("users").document(userId).set(user).await()
        }
    }

    suspend fun getCurrentUser() : Result<User>{

        val userId = auth.currentUser?.uid

        if (userId != null) {
            try {
                val userDoc = firestore.collection("users").document(userId).get().await()
                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)
                    if (user != null) {
                        return Result.Success(user)
                    } else {
                        return Result.Fail(Exception("User document exists but failed to convert to User object"))
                    }
                } else {
                    return Result.Fail(Exception("User document not found"))
                }
            } catch (e: Exception) {
                // Log error for debugging
                Log.e(TAG, "Error fetching user document: ${e.message}", e)
                return Result.Fail(e)
            }
        } else {
            return Result.Fail(Exception("User not authenticated"))
        }
    }

    fun signOut(): Result<Boolean> =
        try {
            auth.signOut()
            Result.Success(false)
        }catch (e: Exception){
            Result.Fail(e)
        }

    suspend fun signInViaGoogle(account : GoogleSignInAccount): Result<Boolean> =
        try {

            val idToken = account.idToken
            val authCode = account.serverAuthCode
            val credentials = GoogleAuthProvider.getCredential(
                idToken, authCode
            )

            val signIn = auth.signInWithCredential(credentials).await()

            val usersCollectionSnap = firestore.collection("users").get().await()
            var userExist = false
            usersCollectionSnap.documents.forEach {doc->
                if(doc.id == auth.uid) userExist = true
            }
            if(!userExist){
                saveUserToFirestore(User(account.displayName!!, account.email!!))
            }

            if(signIn.user != null) Result.Success(true)
            else Result.Success(false)

        }
        catch (e: Exception){
            Result.Fail(e)
        }

}
package com.example.traveler.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

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
}
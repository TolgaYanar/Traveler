package com.example.traveler.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessageRepository(private val firestore: FirebaseFirestore) {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun sendMessage(friend: User, message: Message): Result<Unit> = try {
        if(auth.uid != null){
            firestore.collection("users").document(auth.uid!!)
                .collection("messages").document(friend.uid)
                .collection("messages")
                .add(message).await()
            firestore.collection("users").document(friend.uid)
                .collection("messages").document(auth.uid!!)
                .collection("messages")
                .add(message).await()
        }
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Fail(e)
    }

    fun getChatMessages(friend: User): Flow<List<Message>> = callbackFlow {
        if(auth.uid != null){
            val subscription = firestore.collection("users").document(auth.uid!!)
                .collection("messages")
                .document(friend.uid)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, _ ->
                    querySnapshot?.let {
                        trySend(it.documents.map { doc ->
                            doc.toObject(Message::class.java)!!.copy()
                        }).isSuccess
                    }
                }

            awaitClose { subscription.remove() }
        }
    }

}
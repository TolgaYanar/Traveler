package com.example.traveler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.data.Injection
import com.example.traveler.data.Message
import com.example.traveler.data.MessageRepository
import com.example.traveler.data.User
import com.example.traveler.data.Result
import kotlinx.coroutines.launch

class MessageViewModel: ViewModel() {

    private val messageRepository: MessageRepository = MessageRepository(Injection.instance())

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    fun loadMessages(friend: User) {
        viewModelScope.launch {
            messageRepository.getChatMessages(friend)
                .collect { _messages.value = it }
        }
    }

    fun sendMessage(currentUser: User?, text: String, friend: User) {
        if (currentUser != null) {
            val message = Message(
                senderFirstName = currentUser.fullName,
                senderId = currentUser.uid,
                text = text
            )
            viewModelScope.launch {
                when (messageRepository.sendMessage(friend, message)) {
                    is Result.Success -> Unit
                    is Error -> {

                    }

                    else -> {}
                }
            }
        }
    }
}
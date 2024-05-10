package com.example.traveler

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.traveler.data.Message
import com.example.traveler.data.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(friend: User,
               messageViewModel: MessageViewModel = viewModel(),
               profileViewModel: ProfileViewModel,
               navController: NavController
) {

    val messages by messageViewModel.messages.observeAsState(emptyList())
    val currentUser by profileViewModel.currentUser.observeAsState()
    messageViewModel.loadMessages(friend = friend)
    val text = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Image(painter = rememberAsyncImagePainter(model = friend.profile_image), contentDescription = null,
                            modifier = Modifier.size(40.dp), contentScale = ContentScale.Crop)
                    }
                    Text(text = friend.fullName, modifier = Modifier.padding(vertical = 4.dp))
                }
            },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Gray
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Display the chat messages
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(messages) {message->
                    ChatMessageItem(
                        message = message.copy(isSentByCurrentUser =
                        message.senderId == currentUser?.uid),
                        friend = friend
                    )
                }

            }

            // Chat input field and send icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    shape = RoundedCornerShape(20.dp),
                    value = text.value,
                    onValueChange = { text.value = it },
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )

                IconButton(
                    onClick = {
                        // Send the message when the icon is clicked
                        if (text.value.isNotEmpty()) {
                            messageViewModel.sendMessage(currentUser = currentUser, text.value.trim(),
                                friend)
                            text.value = ""
                        }
                        messageViewModel.loadMessages(friend)
                    }
                ){
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val messageDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val now = LocalDateTime.now()

    return when {
        isSameDay(messageDateTime, now) -> "today ${formatTime(messageDateTime)}"
        isSameDay(messageDateTime.plusDays(1), now) -> "yesterday ${formatTime(messageDateTime)}"
        else -> formatDate(messageDateTime)
    }
}

private fun isSameDay(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return dateTime1.format(formatter) == dateTime2.format(formatter)
}

private fun formatTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(dateTime)
}

private fun formatDate(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return formatter.format(dateTime)
}


@Composable
fun ChatMessageItem(message: Message, friend: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (message.isSentByCurrentUser) Alignment.End else Alignment.Start
    ) {
        
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByCurrentUser) colorResource(id = R.color.purple_700) else Color.Gray,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
                .widthIn(max = 220.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = TextStyle(fontSize = 16.sp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message.senderFirstName,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
        Text(
            text = formatTimestamp(message.timestamp), // Replace with actual timestamp logic
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}
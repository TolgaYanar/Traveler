package com.example.traveler

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traveler.data.AlarmItem
import com.example.traveler.data.Injection
import com.example.traveler.data.uploadNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar
import java.util.TimeZone

@Composable
fun NotificationsScreen(navController: NavController,
                        journalPropertiesViewModel: JournalPropertiesViewModel = viewModel()) {

    val bottomNavClass = BottomNavigationClass()
    bottomNavClass.selectedItemIndex = 2

    val notifications by remember {
        mutableStateOf(mutableStateOf(emptyList<AlarmItem>()))
    }

    val currentTime by remember {
        mutableStateOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    }

    LaunchedEffect(key1 = notifications){
        journalPropertiesViewModel.getNotifications(notifications)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Notifications", color = Color.Black,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .heightIn(max = 24.dp)
                        .alpha(0.59f), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }, elevation = 3.dp,
                backgroundColor = colorResource(id = R.color.app_bar_color),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.UserProfileScreen.route)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_person_outline_24), contentDescription = null,
                            modifier = Modifier.size(30.dp,30.dp))
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.UserProfileScreen.route)
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null,
                            modifier = Modifier.size(30.dp,30.dp))
                    }
                })
        },
        bottomBar = {
            NavigationBar {
                bottomNavClass.items.forEachIndexed { index, item ->
                    NavigationBarItem(selected = bottomNavClass.selectedItemIndex == index,
                        onClick = {
                            bottomNavClass.selectedItemIndex = index
                            if(item.title != "Notifications") navController.navigate(item.route)
                        },
                        label = {
                            Text(text = item.title)
                        }, alwaysShowLabel = false,
                        icon = {
                            BadgedBox(badge = {
                                if(item.badgeCount != null) {
                                    Badge {
                                        Text(text = item.badgeCount.toString())
                                    }
                                }
                            }) {
                                Icon(imageVector = if(index == bottomNavClass.selectedItemIndex) item.selectedIcon
                                else item.unselectedIcon, contentDescription = null)
                            }
                        })
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {

            if (notifications.value.isNotEmpty()){
                item {
                    Text(text = "Today", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .alpha(0.6f)
                            .padding(10.dp))
                }

                notifications.value.forEach {
                    if(it.seen == false){
                        it.seen = true
                        val notificationHash = hashMapOf<String, Any>(
                            "title" to it.title,
                            "startTime" to it.startTime,
                            "message" to it.message,
                            "notified" to it.notified,
                            "seen" to it.seen,
                            "id" to it.id
                        )
                        uploadNotification(it.id, notificationHash)
                    }
                }

                items(notifications.value){notification ->

                    Card(
                        modifier = Modifier
                            .width(300.dp)
                            .height(100.dp)
                            .padding(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if(notification.seen && currentTime >= notification.startTime - 3 * 60 * 60 * 1000){
                                    Color.Red
                                }else {
                                    Color.Green
                                }
                        )
                    ) {
                        Box(modifier = Modifier
                            .padding(5.dp)
                            .fillMaxSize()
                        ){
                            Column(modifier = Modifier.fillMaxSize()) {
                                Row {
                                    Icon(painter = painterResource(id = R.drawable.baseline_notifications_24),
                                        contentDescription = null)
                                    Text(text = journalPropertiesViewModel.longToTime(notification.startTime), fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier
                                    .padding(2.dp)
                                    .requiredWidth(250.dp)) {
                                    Text(text = notification.title, fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.End)
                                {
                                    Text(text = journalPropertiesViewModel.longToTime(notification.notified + 3 * 60 * 60 * 1000), fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold)
                                }
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
fun previi(journalPropertiesViewModel: JournalPropertiesViewModel = viewModel()){
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(100.dp)
            .padding(10.dp)
    ) {
        Box(modifier = Modifier
            .padding(5.dp)
            .fillMaxSize()
        ){
            Column(modifier = Modifier.fillMaxSize()) {
                Row {
                    Icon(painter = painterResource(id = R.drawable.baseline_notifications_24),
                        contentDescription = null)
                    Text(text = journalPropertiesViewModel.longToTime(1713578820000), fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier
                    .padding(2.dp)
                    .requiredWidth(250.dp)) {
                    Text(text = "Tatilden Donus Seremonisi", fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End)
                {
                    Text(text = journalPropertiesViewModel.longToTime(1713567720000), fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}